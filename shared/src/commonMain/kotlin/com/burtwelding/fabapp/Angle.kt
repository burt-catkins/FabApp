package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class Angle (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: AngleUnitType? = AngleUnitType.DEGREE,
    override var displayName: String? = null,
    override var variableName: String? = null
): Number<AngleUnitType>()
