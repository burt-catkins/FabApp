package com.burtwelding.fabapp.number

import deci.trim
import org.kimplify.deci.Deci

private tailrec fun greatestCommonDenominator(a: Deci, b: Deci): Deci = if (b.isZero()) a else greatestCommonDenominator(b, a % b)

data class Fraction (val originalNumerator: Deci, val originalDenominator: Deci) {
    class DivideByZero (
        message: String,
        cause: Throwable? = null,
    ) : RuntimeException(message, cause)
    val numerator: Deci
    val denominator:Deci
    init {
        if (originalDenominator.isZero()) throw DivideByZero("why u tryna give me a fraction with a 0 denominator??? boy WHAAAAT")
        val gcd = greatestCommonDenominator(originalNumerator.abs(), originalDenominator.abs())
        val sign = if ((originalNumerator < Deci.ZERO) xor (originalDenominator < Deci.ZERO)) Deci.ONE.unaryMinus() else Deci.ONE
        numerator = sign * originalNumerator.abs() / gcd
        denominator = originalDenominator.abs() / gcd
    }
    override fun toString() = if (numerator == Deci.ZERO) "" else "${numerator.trim(3)}/${denominator.trim(3)}"
}
