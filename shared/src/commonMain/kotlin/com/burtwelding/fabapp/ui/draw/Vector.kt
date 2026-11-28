package com.burtwelding.fabapp.ui.draw

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.number.Number
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci
import org.kimplify.deci.math.sqrt

open class Vector (
    var x: Deci,
    var y: Deci
) {
    constructor (x: Number, y: Number): this (x.toDeci(), y.toDeci())
    constructor (x: Number, y: Deci): this (x.toDeci(), y)
    constructor (x: Deci, y: Number): this (x, y.toDeci())
    fun translate (to: Vector) {
        this.x += to.x
        this.y += to.y
    }
    /**
     * I'm forcing the "about" to force you to think about that (no pun intended), cuz it's easy to forget, plus it's easy to assume that it's rotating around it's center (spoiler: that's a lot harder than it seems).
     * If the shape is already relative to an origin without translation, then just set about = Vector(0,0)
     */
    fun rotate (degrees: Deci, about: Vector) {
        val radians = convertDegreesToRadians(degrees)
        this.x = (this.x - about.x) * cos(radians) - (this.y - about.y) * sin(radians)
        this.y = (this.x - about.x) * sin(radians) + (this.y - about.y) * cos(radians)
    }
    fun scale (scalar: Deci) {
        this.x *= scalar
        this.y *= scalar
    }
    fun magnitude () = (this.x * this.x + this.y * this.y).sqrt()
}