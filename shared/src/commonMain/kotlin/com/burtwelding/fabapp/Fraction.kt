package com.burtwelding.fabapp

import org.kimplify.deci.Deci
import org.kimplify.deci.RoundingMode

private tailrec fun greatestCommonDenominator(a: Deci, b: Deci): Deci = if (b.isZero()) a else greatestCommonDenominator(b, a % b)

data class Fraction (val originalNumerator: Deci, val originalDenominator: Deci) {
    val numerator: Deci
    val denominator:Deci
    init {
        if (originalNumerator.minus(originalNumerator.setScale(0, RoundingMode.FLOOR)) != Deci.ZERO
            || originalDenominator.minus(originalDenominator.setScale(0, RoundingMode.DOWN)) != Deci.ZERO)
            throw IllegalArgumentException("bruh num/denom aint whole numbers yo! num: $originalNumerator / denom: $originalDenominator")
        require(!originalDenominator.isZero()) { "Denominator cannot be zero." }
        require((originalNumerator % Deci.ONE).isZero()) { "Numerator must be an integer" }
        require((originalDenominator % Deci.ONE).isZero()) { "Denominator must be an integer" }
        val gcd = greatestCommonDenominator(originalNumerator.abs(), originalDenominator.abs())
        val sign = if ((originalNumerator < Deci.ZERO) xor (originalDenominator < Deci.ZERO)) Deci.ONE.unaryMinus() else Deci.ONE
        numerator = sign * originalNumerator.abs() / gcd
        denominator = originalDenominator.abs() / gcd
    }
    override fun toString(): String = if (denominator == Deci.ONE) numerator.trim(3) else "${numerator.trim(3)}/${denominator.trim(3)}"
}
