package com.burtwelding.fabapp.item

import com.burtwelding.fabapp.AngleUnitType
import org.kimplify.deci.Deci

data class Angle (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: AngleUnitType? = AngleUnitType.DEGREE,
    override var displayName: String? = null,
    override var variableName: String? = null
): Number<AngleUnitType>()
