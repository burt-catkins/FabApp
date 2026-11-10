package com.burtwelding.fabapp.item

import com.burtwelding.fabapp.UnitType
import org.kimplify.deci.Deci

abstract class Number <U: UnitType>: Item() {
    abstract var baseUnitDecimalValue: Deci
    abstract var displayUnits: U?
}