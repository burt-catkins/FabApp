package com.burtwelding.fabapp.ui.draw

import com.burtwelding.fabapp.number.Number
import org.kimplify.deci.Deci

data class Vector (
    val x: Deci,
    val y: Deci
) {
    constructor (x: Number, y: Number): this (x.toDeci(), y.toDeci())
    constructor (x: Number, y: Deci): this (x.toDeci(), y)
    constructor (x: Deci, y: Number): this (x, y.toDeci())
}