package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.Item

class ItemHandle private constructor(
    private var source: Source
) {
    private sealed interface Source {
        data class Direct(val item: Item) : Source
        data class Saved(val key: String) : Source
    }

    companion object {
        fun direct(item: Item) = ItemHandle(Source.Direct(item))
        fun saved(key: String) = ItemHandle(Source.Saved(key))
    }

    val value: Item?
        get() = when (val s = source) {
            is Source.Direct -> s.item
            is Source.Saved -> TempItemMap.items[s.key]
        }

    fun save(key: String) {
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
}