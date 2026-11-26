package com.burtwelding.fabapp

import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants

object Calculator {
    fun convertRadiansToDegrees (radianAmount: Deci) =
        radianAmount.times(Deci(180)).div(DeciConstants.PI)
    fun convertDegreesToRadians (degreeAmount: Deci) =
        degreeAmount.times(DeciConstants.PI).div(Deci(180))
}
