package com.burtwelding.fabapp.item

import com.burtwelding.fabapp.AngleUnitType
import kotlinx.serialization.Serializable
import org.kimplify.deci.Deci

@Serializable
data class Angle (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: AngleUnitType? = AngleUnitType.DEGREE,
): Number<AngleUnitType>()
