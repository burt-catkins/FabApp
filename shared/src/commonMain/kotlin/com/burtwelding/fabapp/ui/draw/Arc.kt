package com.burtwelding.fabapp.ui.draw

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci

/**
 * Degrees are used as standard unit circle mathematics, meaning positive degrees are counter-clockwise.
 */
open class Arc {
    val radius: Deci
    val center: Vector
    val startDegrees: Deci
    val sweepDegrees: Deci

    /**
     * This constructor adjusts the start and sweep so that they are within 360 degrees and are both positive for EZ development.
     */
    constructor (radius: Deci, center: Vector, startDegrees: Deci, sweepDegrees: Deci) {
        this.radius = radius
        this.center = center
        this.startDegrees = Deci(360) + run {
            if (sweepDegrees > Deci.ZERO) startDegrees
            else sweepDegrees
        } % Deci(360)
        this.sweepDegrees = Deci(360) + run {
            if (sweepDegrees > Deci.ZERO) sweepDegrees
            else startDegrees
        } % Deci(360)
    }
    fun withinArcRange (angleDegrees: Deci) = Deci(360) + (angleDegrees - this.startDegrees) % Deci(360) <= this.sweepDegrees
}