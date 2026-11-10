package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.item.Item
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.uuid.Uuid

@Serializable(with = ItemHandle.Serializer::class)
class ItemHandle<T : Item> private constructor(
    private var source: Source<T>,
    private val serializer: KSerializer<T>
) {
    private sealed interface Source<out T : Item> {
        data class Direct<T : Item>(val item: T) : Source<T>
        data class Saved(val key: Uuid) : Source<Nothing>
    }

    companion object {
        inline operator fun <reified T : Item> invoke(item: T): ItemHandle<T> = direct(item)

        inline operator fun <reified T : Item> invoke(key: Uuid): ItemHandle<T> = saved(key)

        inline fun <reified T : Item> direct(item: T): ItemHandle<T> = direct(item, serializer<T>())

        inline fun <reified T : Item> saved(key: Uuid): ItemHandle<T> = saved(key, serializer<T>())

        @PublishedApi
        internal fun <T : Item> direct(item: T, serializer: KSerializer<T>): ItemHandle<T> = ItemHandle(Source.Direct(item), serializer)

        @PublishedApi
        internal fun <T : Item> saved(key: Uuid, serializer: KSerializer<T>): ItemHandle<T> = ItemHandle(Source.Saved(key), serializer)
    }

    /** Synchronous access; saved keys can block. Use resolve() from coroutines. */
    val item: T
        get() = when (val s = source) {
            is Source.Direct -> s.item
            is Source.Saved -> runBlocking { load(s.key) }
        }

    /** Loads saved references off the calling thread; direct handles need no storage setup. */
    suspend fun resolve(): T = when (val s = source) {
        is Source.Direct -> s.item
        is Source.Saved -> load(s.key)
    }

    internal val savedKey: Uuid? get() = (source as? Source.Saved)?.key
    private suspend fun load(key: Uuid): T = try {
        Storage.value("items/$key", storageSerializer(serializer)).getOrNull() ?: throw NullPointerException("Item no longer saved!")
    } catch (error: StoredValueSerializationException) {
        if (error.cause is ItemTypeMismatchException) throw NullPointerException("Item no longer saved!")
        throw error
    }

    fun save(key: Uuid) {
        when (val s = source) {
            is Source.Direct -> {
                Storage.value<Item>("items/$key").insertBlocking(s.item)
                source = Source.Saved(key)
            }

            is Source.Saved -> { require(s.key == key) { "Item already references another key" } }
        }
    }

    fun save() = save(savedKey ?: Uuid.random())

    @OptIn(ExperimentalSerializationApi::class)
    internal class Serializer<T : Item>(itemSerializer: KSerializer<T>) : KSerializer<ItemHandle<T>> {
        @Suppress("UNCHECKED_CAST")
        private val serializer = itemSerializer.descriptor.capturedKClass?.let {
            serializer(it, emptyList(), false) as KSerializer<T>
        } ?: itemSerializer
        private val delegate = HandleData.serializer(serializer)
        override val descriptor = delegate.descriptor

        override fun serialize(encoder: Encoder, value: ItemHandle<T>) {
            val expectedType = value.serializer.descriptor.serialName
            val data = when (val source = value.source) {
                is Source.Direct -> HandleData(expectedType, direct = source.item)
                is Source.Saved -> HandleData<T>(expectedType, key = source.key)
            }
            encoder.encodeSerializableValue(delegate, data)
        }

        override fun deserialize(decoder: Decoder): ItemHandle<T> {
            val data = decoder.decodeSerializableValue(delegate)
            require(data.expectedType == serializer.descriptor.serialName) { "Unexpected handle type" }
            require((data.key == null) != (data.direct == null)) { "A handle needs exactly one of key or direct value" }
            return if (data.key != null) saved(data.key, serializer)
            else direct(checkNotNull(data.direct), serializer)
        }
    }
}

@Serializable
private data class HandleData<T : Item>(val expectedType: String, val key: Uuid? = null, val direct: T? = null)
