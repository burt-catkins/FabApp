package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class ImperialFractionalResult (
    val inches: Deci,
    val fraction: Fraction,
    val roundResult: RoundResult
)
