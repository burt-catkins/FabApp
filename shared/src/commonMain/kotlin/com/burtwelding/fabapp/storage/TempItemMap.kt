package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.item.Item
import kotlin.uuid.Uuid

object TempItemMap {
    val items: MutableMap<Uuid, Item> = mutableMapOf()
}