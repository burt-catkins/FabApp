package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class Angle (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: AngleUnitType?,
    override var displayName: String?,
    override var variableName: String?
): Number<AngleUnitType>
