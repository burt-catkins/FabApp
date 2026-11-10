package com.burtwelding.fabapp

import deci.trim
import org.kimplify.deci.Deci

data class ImperialFractionalResult (
    val inches: Deci,
    val fraction: Fraction?,
    val roundResult: RoundResult
) {
    override fun toString() =
        if (inches == Deci.ZERO) {
            fraction?.toString() ?: "0"
        } else if (fraction == null) {
            inches.trim(3)
        } else {
            "$inches-${fraction.numerator.trim(3)}/${fraction.denominator.trim(3)}"
        }

}
