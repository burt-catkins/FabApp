package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.item.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.concurrent.Volatile
import kotlin.uuid.Uuid

@Serializable(with = ItemHandle.Serializer::class)
class ItemHandle<T : Item> private constructor(
    @Volatile private var source: Source<T>,
    private val serializer: KSerializer<T>
) {
    private val saveMutex = Mutex()

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

    private fun storedItem(key: Uuid = checkNotNull(savedKey) { "Save the item first" }): StoredValue<T> =
        Storage.value("items/$key", storageSerializer(serializer))

    private suspend fun load(key: Uuid): T = try {
        storedItem(key).getOrNull() ?: throw NullPointerException("Item no longer saved!")
    } catch (error: StoredValueSerializationException) {
        if (error.cause is ItemTypeMismatchException) throw NullPointerException("Item no longer saved!")
        throw error
    }

    /** Saves the item and returns its UUID. An already saved handle keeps its key. */
    suspend fun save(key: Uuid? = null): Uuid = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            when (val s = source) {
                is Source.Direct -> {
                    val id = key ?: Uuid.random()
                    // Publish the saved reference even if the caller cancels during the write.
                    withContext(NonCancellable) {
                        storedItem(id).insert(s.item)
                        source = Source.Saved(id)
                    }
                    id
                }

                is Source.Saved -> {
                    require(key == null || s.key == key) { "Item already references another key" }
                    s.key
                }
            }
        }
    }

    /** Synchronous save. Blocks the calling thread until the UUID is available. */
    fun saveBlocking(key: Uuid? = null): Uuid = runBlocking { save(key) }

    /** Atomically edits a saved item. The transform must not call storage. */
    suspend fun update(transform: (T) -> T): T = storedItem().update(transform)

    /** Deletes the saved item. References keep their key and will fail to resolve. */
    suspend fun remove() = storedItem().removeWithTypeCheck()

    fun updateBlocking(transform: (T) -> T): T = runBlocking { update(transform) }
    fun removeBlocking() = runBlocking { remove() }

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
