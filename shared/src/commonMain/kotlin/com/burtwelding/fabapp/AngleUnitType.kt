package com.burtwelding.fabapp

import kotlinx.serialization.Serializable

@Serializable
enum class AngleUnitType: UnitType {
    DEGREE,
    RADIAN
}