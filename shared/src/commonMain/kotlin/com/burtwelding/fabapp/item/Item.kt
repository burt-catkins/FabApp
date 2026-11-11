package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

@Serializable
sealed class Item {
    open var displayName: String? = null
    open var variableName: String? = null
}