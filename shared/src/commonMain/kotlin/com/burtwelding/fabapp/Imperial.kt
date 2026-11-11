package com.burtwelding.fabapp

import kotlinx.serialization.Serializable

@Serializable
enum class Imperial: LengthUnitType {
    INCH,
    FOOT,
    YARD,
}