package com.burtwelding.fabapp

import org.kimplify.deci.Deci

data class Vector (
    val x: Deci,
    val y: Deci
) {
    constructor (x: Number, y: Number): this (Deci(x.toString()), Deci(y.toString()))
    constructor (x: Number, y: Deci): this (Deci(x.toString()), y)
    constructor (x: Deci, y: Number): this (x, Deci(y.toString()))
}