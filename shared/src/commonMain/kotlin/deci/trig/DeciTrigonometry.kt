package deci.trig

import org.kimplify.deci.Deci
import org.kimplify.deci.DeciContext
import org.kimplify.deci.exception.DeciArithmeticException
import kotlin.jvm.JvmName

/**
 * Returns the sine of this angle in radians, rounded using [context].
 *
 * These extensions keep the installed Deci library unchanged. They use private,
 * common-Kotlin decimal interval arithmetic, without converting through Double.
 * The result is returned only when both interval endpoints round identically.
 *
 * [DeciContext.precision] keeps Deci's meaning: fractional digits in the result.
 * A Deci value does not carry a context; pass the same context explicitly when
 * chaining operations that must share a rounding policy.
 *
 * Working precision adapts to the input and requested context. There is no
 * extension-specific digit or magnitude cap. The installed Deci backend must
 * be able to store the rounded result exactly; otherwise an exception is thrown
 * instead of silently losing digits. Existing backend and memory limits apply.
 * Digits lost before this function receives the Deci value cannot be recovered.
 *
 * @throws DeciArithmeticException if the input is nonfinite, the rounded result
 * is not representable by this Deci backend, or a required size exceeds Kotlin's
 * integer indexing range. Resource exhaustion remains a runtime limitation.
 */
@JvmName("DeciSineExtension")
fun Deci.sin(context: DeciContext = DeciContext.DEFAULT): Deci =
    calculateTrig(TrigFunction.SIN, context)

/**
 * Returns the cosine of this angle in radians, rounded using [context].
 * Uses the same context and representability rules as [sin].
 */
@JvmName("DeciCosineExtension")
fun Deci.cos(context: DeciContext = DeciContext.DEFAULT): Deci =
    calculateTrig(TrigFunction.COS, context)

/**
 * Returns the principal arcsine in radians, rounded using [context].
 * The mathematical result lies in [-pi/2, pi/2]; final rounding can move it
 * slightly outside that interval. Inputs must be in [-1, 1] and are never clamped.
 * Uses the same context and representability rules as [sin].
 *
 * @throws DeciArithmeticException if this value is outside [-1, 1].
 */
@JvmName("DeciArcsineExtension")
fun Deci.asin(context: DeciContext = DeciContext.DEFAULT): Deci =
    calculateTrig(TrigFunction.ASIN, context)

/**
 * Returns the principal arccosine in radians, rounded using [context].
 * The mathematical result lies in [0, pi]; final rounding can move it slightly
 * above pi. Inputs must be in [-1, 1] and are never clamped.
 * Uses the same context and representability rules as [sin].
 *
 * @throws DeciArithmeticException if this value is outside [-1, 1].
 */
@JvmName("DeciArccosineExtension")
fun Deci.acos(context: DeciContext = DeciContext.DEFAULT): Deci =
    calculateTrig(TrigFunction.ACOS, context)

/**
 * Returns the principal arctangent in radians, rounded using [context].
 * The mathematical result lies in (-pi/2, pi/2); final rounding can reach or
 * slightly exceed an endpoint. Uses the same context and representability
 * rules as [sin]. Every finite real input is in the mathematical domain.
 */
@JvmName("DeciArctangentExtension")
fun Deci.atan(context: DeciContext = DeciContext.DEFAULT): Deci =
    calculateTrig(TrigFunction.ATAN, context)

private fun Deci.calculateTrig(function: TrigFunction, context: DeciContext): Deci {
    val input = TrigInput.parse(toString())
    if (function == TrigFunction.ASIN || function == TrigFunction.ACOS) {
        if (input.compareMagnitudeToOne() > 0) {
            throw DeciArithmeticException("${function.name.lowercase()} requires an input in [-1, 1]")
        }
    }
    // Large forward angles amplify pi uncertainty during quadrant reduction.
    // Reciprocal reduction lets atan avoid paying that same precision cost.
    val reductionDigits = if (function == TrigFunction.SIN || function == TrigFunction.COS) {
        maxOf(0L, input.digits.length.toLong() - input.scale)
    } else {
        0L
    }
    var workingScale = trigCheckedSize(
        maxOf(32L, context.precision.toLong() + reductionDigits + 24L, input.scale.toLong() + 16L),
    )
    while (true) {
        val text = TrigDecimalMath(workingScale).rounded(function, input, context)
        if (text != null) {
            val intended = TrigInput.parse(text)
            val result = try {
                Deci(text)
            } catch (exception: Exception) {
                throw DeciArithmeticException("This Deci backend could not store the rounded trig result", exception)
            }
            val stored = TrigInput.parse(result.toString())
            if (stored != intended) {
                throw DeciArithmeticException(
                    "This Deci backend cannot preserve the ${function.name.lowercase()} result rounded to " +
                        "${context.precision} fractional digits; use a context with fewer fractional digits",
                )
            }
            return result
        }
        workingScale = trigCheckedSize(maxOf(workingScale.toLong() + 32L, workingScale.toLong() * 2L))
    }
}

/** Checked sizes prevent a very large request from wrapping into a smaller one. */
internal fun trigCheckedSize(size: Long): Int {
    if (size !in 0L..Int.MAX_VALUE.toLong()) {
        throw DeciArithmeticException("Deci trig requires a size outside Kotlin's integer indexing range")
    }
    return size.toInt()
}

internal enum class TrigFunction { SIN, COS, ASIN, ACOS, ATAN }

/** Magnitude is digits * 10^(-scale), with no leading or trailing coefficient zeros. */
internal data class TrigInput(val negative: Boolean, val digits: String, val scale: Int) {
    val isZero: Boolean get() = digits == "0"

    fun compareMagnitudeToOne(): Int {
        if (isZero) return -1
        val integerPlaces = digits.length.toLong() - scale
        return when {
            integerPlaces < 1 -> -1
            integerPlaces > 1 -> 1
            digits == "1" && scale == 0 -> 0
            else -> 1
        }
    }

    fun fixedMagnitude(workingScale: Int): TrigInteger =
        TrigInteger.parseMagnitude(digits).shiftDecimal(trigCheckedSize(workingScale.toLong() - scale))

    companion object {
        private val NUMBER = Regex("([+-]?)([0-9]+)(?:\\.([0-9]*))?(?:[eE]([+-]?[0-9]+))?")

        fun parse(value: String): TrigInput {
            val match = NUMBER.matchEntire(value)
                ?: throw DeciArithmeticException("Deci trig requires a finite decimal value")
            val fraction = match.groupValues[3]
            val exponentText = match.groupValues[4]
            val exponent = if (exponentText.isEmpty()) 0L else exponentText.toLongOrNull()
                ?: throw DeciArithmeticException("Deci trig input exponent is outside supported range")
            val coefficient = (match.groupValues[2] + fraction).trimStart('0')
            if (coefficient.isEmpty()) return TrigInput(false, "0", 0)
            val trimmed = coefficient.trimEnd('0')
            val baseScale = fraction.length.toLong() - (coefficient.length - trimmed.length)
            // Establish the final Int range before subtracting, so even an
            // extreme exponent cannot overflow Long into an apparently valid size.
            if (exponent < baseScale - Int.MAX_VALUE.toLong() || exponent > baseScale - Int.MIN_VALUE.toLong()) {
                throw DeciArithmeticException("Deci trig input scale is outside Kotlin's integer indexing range")
            }
            val scale = baseScale - exponent
            return TrigInput(match.groupValues[1] == "-", trimmed, scale.toInt())
        }
    }
}

/**
 * hehe
 */
fun sin(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci = x.sin(context)

/**
 * bruh
 */
fun cos(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci = x.cos(context)

/**
 * lol
 */
fun asin(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci = x.asin(context)

/**
 * today, i woke up and went downstairs and madea cup of coffe. thank u for watching
 */
fun acos(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci = x.acos(context)

/**
 * hi
 */
fun atan(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci = x.atan(context)

/**
 * who put this here
 */
fun tan(
    x: Deci,
    context: DeciContext = DeciContext.DEFAULT,
): Deci {
    val sine = sin(x, context)
    val cosine = cos(x, context)

    if (cosine.isZero()) {
        throw DeciArithmeticException("Tangent is undefined at $x")
    }

    return sine.divide(cosine, context)
}
//fun tan(
//    x: Deci,
//    context: DeciContext = DeciContext.DEFAULT,
//): Deci = x.tan(context)