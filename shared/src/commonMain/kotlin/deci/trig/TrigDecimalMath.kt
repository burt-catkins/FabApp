package deci.trig

import org.kimplify.deci.DeciContext
import org.kimplify.deci.RoundingMode
import org.kimplify.deci.exception.DeciArithmeticException

/**
 * Outward-rounded fixed-point intervals. All endpoints are integers scaled by
 * 10^scale. Keeping this arithmetic independent of Deci avoids backend precision
 * differences during evaluation; only the final decimal string becomes a Deci.
 */
internal class TrigDecimalMath(private val scale: Int) {
    private val unit = TrigInteger.powerOfTen(scale)
    private val zero = point(TrigInteger.ZERO)
    private val one = point(unit)
    private val tenth = unit.divideSmall(10).first
    private val tailUnits = TrigInteger.of(2)
    // The slowest series here is atan(1/5), which gains more than one decimal
    // digit per term. Grow the allowance with working precision instead of
    // imposing a fixed term limit on large contexts.
    private val maxTerms = trigCheckedSize(scale.toLong() + 32L)

    // Machin's identity. Both arctangents and all arithmetic retain bounds;
    // no finite PI literal is assumed to equal mathematical pi.
    private val pi: Interval by lazy(LazyThreadSafetyMode.NONE) {
        atanSeries(one / 5) * 16 - atanSeries(one / 239) * 4
    }

    fun rounded(function: TrigFunction, input: TrigInput, context: DeciContext): String? {
        val magnitude = point(input.fixedMagnitude(scale))
        val value = when (function) {
            TrigFunction.SIN -> {
                if (input.isZero) zero else forward(magnitude, sine = true)?.withSign(input.negative) ?: return null
            }
            TrigFunction.COS -> if (input.isZero) one else forward(magnitude, sine = false) ?: return null
            TrigFunction.ASIN -> {
                val positive = when {
                    input.isZero -> zero
                    input.compareMagnitudeToOne() == 0 -> pi / 2
                    else -> atanReduced(magnitude / (one + squareRoot((one - magnitude) * (one + magnitude)))) * 2
                }
                positive.withSign(input.negative)
            }
            TrigFunction.ACOS -> when {
                input.isZero -> pi / 2
                input.compareMagnitudeToOne() == 0 -> if (input.negative) pi else zero
                else -> {
                    val positive = atanReduced(squareRoot((one - magnitude) / (one + magnitude))) * 2
                    if (input.negative) pi - positive else positive
                }
            }
            TrigFunction.ATAN -> {
                val positive = when {
                    input.isZero -> zero
                    input.compareMagnitudeToOne() == 0 -> pi / 4
                    input.compareMagnitudeToOne() > 0 -> pi / 2 - atanReduced(one / magnitude)
                    else -> atanReduced(magnitude)
                }
                positive.withSign(input.negative)
            }
        }
        val lower = roundInteger(value.lower, context)
        val upper = roundInteger(value.upper, context)
        return if (lower.compareTo(upper) == 0) lower.toDecimalString(context.precision) else null
    }

    private fun forward(x: Interval, sine: Boolean): Interval? {
        val halfPi = pi / 2
        val quotient = x / halfPi
        val half = unit.divideSmall(2).first
        val lowerQuadrant = (quotient.lower + half).floorDropDecimal(scale)
        val upperQuadrant = (quotient.upper + half).floorDropDecimal(scale)
        if (lowerQuadrant.compareTo(upperQuadrant) != 0) return null
        val residual = x - halfPi.timesInteger(lowerQuadrant)
        // The nearest-quadrant residual is within pi/4; the rational bound of 1
        // is sufficient to justify decreasing alternating Taylor terms.
        if (residual.maxAbs() > unit) return null
        return when (lowerQuadrant.divideSmall(4).second) {
            0 -> if (sine) sinSeries(residual) else cosSeries(residual)
            1 -> if (sine) cosSeries(residual) else -sinSeries(residual)
            2 -> if (sine) -sinSeries(residual) else -cosSeries(residual)
            else -> if (sine) -cosSeries(residual) else sinSeries(residual)
        }
    }

    private fun sinSeries(x: Interval): Interval {
        val squared = square(x)
        var term = x
        var sum = x
        for (n in 1..maxTerms) {
            val twice = trigCheckedSize(2L * n)
            val next = -(term * squared) / twice / trigCheckedSize(2L * n + 1L)
            if (next.maxAbs() <= tailUnits) return sum + next.withZero()
            sum += next
            term = next
        }
        convergenceFailure()
    }

    private fun cosSeries(x: Interval): Interval {
        val squared = square(x)
        var term = one
        var sum = one
        for (n in 1..maxTerms) {
            val twice = trigCheckedSize(2L * n)
            val next = -(term * squared) / (twice - 1) / twice
            if (next.maxAbs() <= tailUnits) return sum + next.withZero()
            sum += next
            term = next
        }
        convergenceFailure()
    }

    private fun atanReduced(value: Interval): Interval {
        var x = value
        var multiplier = 1
        // atan(x) = 2*atan(x/(1+sqrt(1+x*x))). Check the upper bound,
        // so the entire interval is small enough for the power series.
        repeat(16) {
            if (x.upper <= tenth) return atanSeries(x) * multiplier
            x /= one + squareRoot(one + square(x))
            multiplier *= 2
        }
        convergenceFailure()
    }

    private fun atanSeries(x: Interval): Interval {
        check(x.lower.sign >= 0 && x.upper < unit)
        val squared = square(x)
        var power = x
        var sum = x
        for (n in 1..maxTerms) {
            power *= squared
            val unsignedTerm = power / trigCheckedSize(2L * n + 1L)
            val next = if (n % 2 == 1) -unsignedTerm else unsignedTerm
            // An alternating tail lies between zero and the next term,
            // not at the next term itself.
            if (next.maxAbs() <= tailUnits) return sum + next.withZero()
            sum += next
        }
        convergenceFailure()
    }

    private fun square(x: Interval): Interval {
        val a = x.lower * x.lower
        val b = x.upper * x.upper
        val low = if (x.lower.sign <= 0 && x.upper.sign >= 0) TrigInteger.ZERO else minOf(a, b)
        return Interval(low.floorDropDecimal(scale), maxOf(a, b).ceilDropDecimal(scale))
    }

    private fun squareRoot(x: Interval): Interval {
        check(x.lower.sign >= 0)
        val low = x.lower.shiftDecimal(scale).squareRootFloor()
        val highRadicand = x.upper.shiftDecimal(scale)
        val highRoot = highRadicand.squareRootFloor()
        val high = if ((highRoot * highRoot).compareTo(highRadicand) == 0) highRoot else highRoot + TrigInteger.ONE
        return Interval(low, high)
    }

    private operator fun Interval.plus(other: Interval): Interval = Interval(lower + other.lower, upper + other.upper)

    private operator fun Interval.minus(other: Interval): Interval = Interval(lower - other.upper, upper - other.lower)

    private operator fun Interval.unaryMinus(): Interval = Interval(-upper, -lower)

    private fun Interval.withSign(negative: Boolean): Interval = if (negative) -this else this

    private operator fun Interval.times(other: Interval): Interval {
        val products = listOf(lower * other.lower, lower * other.upper, upper * other.lower, upper * other.upper)
        return Interval(products.minOrNull()!!.floorDropDecimal(scale), products.maxOrNull()!!.ceilDropDecimal(scale))
    }

    private operator fun Interval.times(value: Int): Interval {
        require(value >= 0)
        return Interval(lower.timesSmall(value), upper.timesSmall(value))
    }

    private fun Interval.timesInteger(value: TrigInteger): Interval {
        require(value.sign >= 0)
        return Interval(lower * value, upper * value)
    }

    private operator fun Interval.div(value: Int): Interval =
        Interval(lower.floorDivideSmall(value), upper.ceilDivideSmall(value))

    private operator fun Interval.div(other: Interval): Interval {
        // Every non-integer divisor in these algorithms is strictly positive.
        check(other.lower.sign > 0)
        val lowDivisor = if (lower.sign >= 0) other.upper else other.lower
        val highDivisor = if (upper.sign >= 0) other.lower else other.upper
        return Interval(
            lower.shiftDecimal(scale).floorDivide(lowDivisor),
            upper.shiftDecimal(scale).ceilDivide(highDivisor),
        )
    }

    private fun roundInteger(value: TrigInteger, context: DeciContext): TrigInteger {
        val discardedPlaces = scale - context.precision
        val magnitude = value.abs()
        val (whole, discarded) = magnitude.dropDecimal(discardedPlaces)
        if (!discarded) return if (value.sign < 0) -whole else whole
        val remainder = magnitude - whole.shiftDecimal(discardedPlaces)
        val halfComparison = remainder.timesSmall(2).compareTo(TrigInteger.powerOfTen(discardedPlaces))
        val increment = when (context.roundingMode) {
            RoundingMode.UP -> true
            RoundingMode.DOWN -> false
            RoundingMode.CEILING -> value.sign > 0
            RoundingMode.FLOOR -> value.sign < 0
            RoundingMode.HALF_UP -> halfComparison >= 0
            RoundingMode.HALF_DOWN -> halfComparison > 0
            RoundingMode.HALF_EVEN -> halfComparison > 0 || (halfComparison == 0 && whole.divideSmall(2).second == 1)
        }
        val rounded = if (increment) whole + TrigInteger.ONE else whole
        return if (value.sign < 0) -rounded else rounded
    }

    private fun point(value: TrigInteger): Interval = Interval(value, value)

    private fun convergenceFailure(): Nothing =
        throw DeciArithmeticException("Deci trig could not enclose the result at the current working precision")

    private data class Interval(val lower: TrigInteger, val upper: TrigInteger) {
        init { check(lower <= upper) }

        fun maxAbs(): TrigInteger = maxOf(lower.abs(), upper.abs())

        fun withZero(): Interval = Interval(minOf(lower, TrigInteger.ZERO), maxOf(upper, TrigInteger.ZERO))
    }

}
