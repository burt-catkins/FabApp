package com.burtwelding.fabapp.number

import deci.trig.acos
import deci.trig.asin
import deci.trig.atan
import deci.trig.cos
import deci.trig.sin
import deci.trig.tan
import org.kimplify.deci.Deci

class Number {
    val number: Deci?
    val fraction: Fraction?
    constructor (decimal: Deci) {
        this.number = decimal
        this.fraction = null
    }
    constructor (decimal: Deci?, fraction: Fraction?) {
        this.number = decimal
        this.fraction = fraction
    }
    fun toDeci () = (this.number?: Deci.ZERO).plus(this.fraction?.numerator?.div(this.fraction.denominator)?: Deci.ZERO)
    operator fun plus (other: Number) = this.toDeci().plus(other.toDeci())
    operator fun plus (other: Deci) = this.toDeci().plus(other)
    operator fun plus (other: kotlin.Number) = this.toDeci().plus(Deci(other.toString()))
    operator fun minus (other: Number) = this.toDeci().minus(other.toDeci())
    operator fun minus (other: Deci) = this.toDeci().minus(other)
    operator fun minus (other: kotlin.Number) = this.toDeci().minus(Deci(other.toString()))
    operator fun div (other: Number) = this.toDeci().div(other.toDeci())
    operator fun div (other: Deci) = this.toDeci().div(other)
    operator fun div (other: kotlin.Number) = this.toDeci().minus(Deci(other.toString()))
    operator fun times (other: Number) = this.toDeci().times(other.toDeci())
    operator fun times (other: Deci) = this.toDeci().times(other)
    operator fun times (other: kotlin.Number) = this.toDeci().times(Deci(other.toString()))
    operator fun rem (other: Number) = this.toDeci().rem(other.toDeci())
    operator fun rem (other: Deci) = this.toDeci().rem(other)
    operator fun rem (other: kotlin.Number) = this.toDeci().rem(Deci(other.toString()))
    operator fun compareTo (other: Number) = this.toDeci().compareTo(other.toDeci())
    operator fun compareTo (other: Deci) = this.toDeci().compareTo(other)
    operator fun compareTo (other: kotlin.Number) = this.toDeci().compareTo(Deci(other.toString()))
    override operator fun equals (other: Any?) = this.toDeci() == when (other) {
        is Deci -> other
        is Number -> other.toDeci()
        is kotlin.Number -> Deci(other.toString())
        else -> error("bro over here tryna compare a Number to a non-number. Bruh! what u doin!")
    }
    fun sin () = toDeci().sin()
    fun cos () = toDeci().cos()
    fun asin () = toDeci().asin()
    fun acos () = toDeci().acos()
    fun atan () = toDeci().atan()
    override fun hashCode(): Int {
        var result = number.hashCode()
        result = 31 * result + fraction.hashCode()
        return result
    }
}

fun sin (number: Number) = sin(number.toDeci())
fun cos (number: Number) = cos(number.toDeci())
fun tan (number: Number) = tan(number.toDeci())
fun asin (number: Number) = asin(number.toDeci())
fun acos (number: Number) = acos(number.toDeci())
fun atan (number: Number) = atan(number.toDeci())