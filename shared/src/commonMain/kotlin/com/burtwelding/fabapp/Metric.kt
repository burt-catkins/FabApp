package com.burtwelding.fabapp

import kotlinx.serialization.Serializable

@Serializable
enum class Metric: LengthUnitType {
    MILLIMETERS,
    CENTIMETERS,
    METERS
}