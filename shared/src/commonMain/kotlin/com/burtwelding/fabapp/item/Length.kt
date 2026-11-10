package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

import com.burtwelding.fabapp.Imperial
import com.burtwelding.fabapp.LengthUnitType
import com.burtwelding.fabapp.Metric
import org.kimplify.deci.Deci

@Serializable
data class Length (
    override var baseUnitDecimalValue: Deci,
    override var displayUnits: LengthUnitType? = null
): Number<LengthUnitType>() {
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
