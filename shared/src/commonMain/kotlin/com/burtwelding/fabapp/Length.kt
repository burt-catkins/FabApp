package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class Length (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: LengthUnitType? = null,
    override var displayName: String? = null,
    override var variableName: String? = null
): Number<LengthUnitType> {
    override fun toString(): String {
        return when (displayUnits) {
            Imperial.INCH -> "$baseUnitDecimalValue\""
            Imperial.FOOT -> "$baseUnitDecimalValue\'"
            Imperial.YARD -> "$baseUnitDecimalValue yd"
            Metric.MILLIMETERS -> "$baseUnitDecimalValue mm"
            Metric.CENTIMETERS -> "$baseUnitDecimalValue cm"
            Metric.METERS -> "$baseUnitDecimalValue m"
            null -> "$baseUnitDecimalValue"
        }
    }
}
