package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.Item
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
import kotlin.uuid.Uuid

class ItemHandle<T : Item> private constructor(
    private var source: Source<T>,
    private val type: KClass<T>
) {
    private sealed interface Source<out T : Item> {
        data class Direct<T : Item>(val item: T) : Source<T>
        data class Saved(val key: Uuid) : Source<Nothing>
    }

    companion object {
        inline operator fun <reified T : Item> invoke(item: T): ItemHandle<T> = direct(item)

        inline operator fun <reified T : Item> invoke(key: Uuid): ItemHandle<T> = saved(key)

        inline fun <reified T : Item> direct(item: T): ItemHandle<T> = direct(item, T::class)

        inline fun <reified T : Item> saved(key: Uuid): ItemHandle<T> = saved(key, T::class)

        @PublishedApi
        internal fun <T : Item> direct(item: T, type: KClass<T>): ItemHandle<T> = ItemHandle(Source.Direct(item), type)

        @PublishedApi
        internal fun <T : Item> saved(key: Uuid, type: KClass<T>): ItemHandle<T> = ItemHandle(Source.Saved(key), type)
    }

    val item: T
        get() = when (val s = source) {
            is Source.Direct -> s.item
            is Source.Saved -> type.safeCast(TempItemMap.items[s.key])?: throw NullPointerException("Item no longer saved!")
        }

    fun save(key: Uuid) {
        when (val s = source) {
            is Source.Direct -> {
                val existing = TempItemMap.items[key]

                require(existing == null || existing === s.item) { "Key already belongs to another item" }

                TempItemMap.items[key] = s.item
                source = Source.Saved(key)
            }

            is Source.Saved -> { require(s.key == key) { "Item already references another key" } }
        }
    }

    fun save() = save(Uuid.random())
}