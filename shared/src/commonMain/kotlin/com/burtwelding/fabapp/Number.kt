package com.burtwelding.fabapp

import org.kimplify.deci.Deci

abstract class Number <U: UnitType>: Item() {
    abstract var baseUnitDecimalValue: Deci
    abstract var displayUnits: U?
}