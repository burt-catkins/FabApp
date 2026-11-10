package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

import com.burtwelding.fabapp.UnitType
import org.kimplify.deci.Deci

@Serializable
sealed class Number <U: UnitType>: Item() {
    abstract var baseUnitDecimalValue: Deci
    abstract var displayUnits: U?
}