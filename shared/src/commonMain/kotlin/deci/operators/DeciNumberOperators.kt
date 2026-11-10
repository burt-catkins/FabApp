package deci.operators

import org.kimplify.deci.Deci

/**
 * Converts Kotlin numeric types to Deci.
 * Float and Double scientific notation is expanded
 * to support Deci versions that reject exponents.
 */
private fun Number.toDeci(): Deci = when (this) {
    is Float, is Double -> Deci(toString().expandExponent())
    else -> Deci(toString())
}

private fun String.expandExponent(): String {
    val e = indexOfAny(charArrayOf('e', 'E'))
    if (e < 0) return this

    val exponent = substring(e + 1).toIntOrNull()
        ?: return this

    val mantissa = substring(0, e)
    val negative = mantissa.startsWith('-')
    val unsigned = mantissa.removePrefix("-").removePrefix("+")
    val dot = unsigned.indexOf('.')
    val point = if (dot < 0) unsigned.length else dot
    val digits = unsigned.replace(".", "")
    val shiftedPoint = point + exponent

    val plain = when {
        shiftedPoint <= 0 ->
            "0." + "0".repeat(-shiftedPoint) + digits

        shiftedPoint >= digits.length ->
            digits + "0".repeat(shiftedPoint - digits.length)

        else ->
            digits.substring(0, shiftedPoint) +
                "." + digits.substring(shiftedPoint)
    }

    return (if (negative) "-" else "") + plain
}

// ---------------------------------------------
// Signed numeric types:
// Byte, Short, Int, Long, Float, Double
// ---------------------------------------------

operator fun Deci.plus(other: Number): Deci =
    this + other.toDeci()

operator fun Number.plus(other: Deci): Deci =
    toDeci() + other

operator fun Deci.minus(other: Number): Deci =
    this - other.toDeci()

operator fun Number.minus(other: Deci): Deci =
    toDeci() - other

operator fun Deci.times(other: Number): Deci =
    this * other.toDeci()

operator fun Number.times(other: Deci): Deci =
    toDeci() * other

operator fun Deci.div(other: Number): Deci =
    this / other.toDeci()

operator fun Number.div(other: Deci): Deci =
    toDeci() / other

operator fun Deci.rem(other: Number): Deci =
    this % other.toDeci()

operator fun Number.rem(other: Deci): Deci =
    toDeci() % other

operator fun Deci.compareTo(other: Number): Int =
    compareTo(other.toDeci())

operator fun Number.compareTo(other: Deci): Int =
    toDeci().compareTo(other)

// ---------------------------------------------
// Unsigned Byte (UByte)
// ---------------------------------------------

operator fun Deci.plus(other: UByte): Deci =
    this + Deci(other.toString())

operator fun UByte.plus(other: Deci): Deci =
    Deci(toString()) + other

operator fun Deci.minus(other: UByte): Deci =
    this - Deci(other.toString())

operator fun UByte.minus(other: Deci): Deci =
    Deci(toString()) - other

operator fun Deci.times(other: UByte): Deci =
    this * Deci(other.toString())

operator fun UByte.times(other: Deci): Deci =
    Deci(toString()) * other

operator fun Deci.div(other: UByte): Deci =
    this / Deci(other.toString())

operator fun UByte.div(other: Deci): Deci =
    Deci(toString()) / other

operator fun Deci.rem(other: UByte): Deci =
    this % Deci(other.toString())

operator fun UByte.rem(other: Deci): Deci =
    Deci(toString()) % other

operator fun Deci.compareTo(other: UByte): Int =
    compareTo(Deci(other.toString()))

operator fun UByte.compareTo(other: Deci): Int =
    Deci(toString()).compareTo(other)

// ---------------------------------------------
// Unsigned Short (UShort)
// ---------------------------------------------

operator fun Deci.plus(other: UShort): Deci =
    this + Deci(other.toString())

operator fun UShort.plus(other: Deci): Deci =
    Deci(toString()) + other

operator fun Deci.minus(other: UShort): Deci =
    this - Deci(other.toString())

operator fun UShort.minus(other: Deci): Deci =
    Deci(toString()) - other

operator fun Deci.times(other: UShort): Deci =
    this * Deci(other.toString())

operator fun UShort.times(other: Deci): Deci =
    Deci(toString()) * other

operator fun Deci.div(other: UShort): Deci =
    this / Deci(other.toString())

operator fun UShort.div(other: Deci): Deci =
    Deci(toString()) / other

operator fun Deci.rem(other: UShort): Deci =
    this % Deci(other.toString())

operator fun UShort.rem(other: Deci): Deci =
    Deci(toString()) % other

operator fun Deci.compareTo(other: UShort): Int =
    compareTo(Deci(other.toString()))

operator fun UShort.compareTo(other: Deci): Int =
    Deci(toString()).compareTo(other)

// ---------------------------------------------
// Unsigned Int (UInt)
// ---------------------------------------------

operator fun Deci.plus(other: UInt): Deci =
    this + Deci(other.toString())

operator fun UInt.plus(other: Deci): Deci =
    Deci(toString()) + other

operator fun Deci.minus(other: UInt): Deci =
    this - Deci(other.toString())

operator fun UInt.minus(other: Deci): Deci =
    Deci(toString()) - other

operator fun Deci.times(other: UInt): Deci =
    this * Deci(other.toString())

operator fun UInt.times(other: Deci): Deci =
    Deci(toString()) * other

operator fun Deci.div(other: UInt): Deci =
    this / Deci(other.toString())

operator fun UInt.div(other: Deci): Deci =
    Deci(toString()) / other

operator fun Deci.rem(other: UInt): Deci =
    this % Deci(other.toString())

operator fun UInt.rem(other: Deci): Deci =
    Deci(toString()) % other

operator fun Deci.compareTo(other: UInt): Int =
    compareTo(Deci(other.toString()))

operator fun UInt.compareTo(other: Deci): Int =
    Deci(toString()).compareTo(other)

// ---------------------------------------------
// Unsigned Long (ULong)
// ---------------------------------------------

operator fun Deci.plus(other: ULong): Deci =
    this + Deci(other.toString())

operator fun ULong.plus(other: Deci): Deci =
    Deci(toString()) + other

operator fun Deci.minus(other: ULong): Deci =
    this - Deci(other.toString())

operator fun ULong.minus(other: Deci): Deci =
    Deci(toString()) - other

operator fun Deci.times(other: ULong): Deci =
    this * Deci(other.toString())

operator fun ULong.times(other: Deci): Deci =
    Deci(toString()) * other

operator fun Deci.div(other: ULong): Deci =
    this / Deci(other.toString())

operator fun ULong.div(other: Deci): Deci =
    Deci(toString()) / other

operator fun Deci.rem(other: ULong): Deci =
    this % Deci(other.toString())

operator fun ULong.rem(other: Deci): Deci =
    Deci(toString()) % other

operator fun Deci.compareTo(other: ULong): Int =
    compareTo(Deci(other.toString()))

operator fun ULong.compareTo(other: Deci): Int =
    Deci(toString()).compareTo(other)
