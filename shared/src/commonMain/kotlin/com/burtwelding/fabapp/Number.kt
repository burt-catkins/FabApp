package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class Number (
    val decimal: Deci,
    val fraction: Fraction?
)
