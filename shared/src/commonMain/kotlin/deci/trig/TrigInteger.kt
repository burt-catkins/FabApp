package deci.trig

/** Small, immutable integer used only by the decimal trigonometry implementation. */
internal class TrigInteger private constructor(
    val sign: Int,
    private val words: IntArray,
) : Comparable<TrigInteger> {
    val isZero: Boolean get() = sign == 0

    fun abs(): TrigInteger = if (sign < 0) TrigInteger(1, words) else this

    operator fun unaryMinus(): TrigInteger = if (isZero) this else TrigInteger(-sign, words)

    override fun compareTo(other: TrigInteger): Int {
        if (sign != other.sign) return sign.compareTo(other.sign)
        return if (isZero) 0 else compareMagnitude(other) * sign
    }

    private fun compareMagnitude(other: TrigInteger): Int {
        if (words.size != other.words.size) return words.size.compareTo(other.words.size)
        for (i in words.lastIndex downTo 0) {
            if (words[i] != other.words[i]) return words[i].compareTo(other.words[i])
        }
        return 0
    }

    operator fun plus(other: TrigInteger): TrigInteger {
        if (isZero) return other
        if (other.isZero) return this
        if (sign != other.sign) {
            val comparison = compareMagnitude(other)
            return when {
                comparison == 0 -> ZERO
                comparison > 0 -> subtractMagnitude(other, sign)
                else -> other.subtractMagnitude(this, other.sign)
            }
        }
        val result = IntArray(trigCheckedSize(maxOf(words.size, other.words.size).toLong() + 1L))
        var carry = 0
        for (i in 0 until result.lastIndex) {
            val sum = words.getOrElse(i) { 0 } + other.words.getOrElse(i) { 0 } + carry
            result[i] = sum % BASE
            carry = sum / BASE
        }
        result[result.lastIndex] = carry
        return normalized(sign, result)
    }

    operator fun minus(other: TrigInteger): TrigInteger = this + (-other)

    private fun subtractMagnitude(other: TrigInteger, resultSign: Int): TrigInteger {
        val result = IntArray(words.size)
        var borrow = 0
        for (i in words.indices) {
            var digit = words[i] - other.words.getOrElse(i) { 0 } - borrow
            borrow = if (digit < 0) 1 else 0
            if (digit < 0) digit += BASE
            result[i] = digit
        }
        check(borrow == 0)
        return normalized(resultSign, result)
    }

    operator fun times(other: TrigInteger): TrigInteger {
        if (isZero || other.isZero) return ZERO
        val result = IntArray(trigCheckedSize(words.size.toLong() + other.words.size))
        for (i in words.indices) {
            var carry = 0
            for (j in other.words.indices) {
                // Base 10,000 keeps this below 100,000,000, safely within Int.
                // Avoiding emulated Long arithmetic matters on Kotlin/JS.
                val product = result[i + j] + words[i] * other.words[j] + carry
                result[i + j] = product % BASE
                carry = product / BASE
            }
            result[i + other.words.size] = carry
        }
        return normalized(sign * other.sign, result)
    }

    fun timesSmall(value: Int): TrigInteger {
        require(value >= 0)
        if (value == 0 || isZero) return ZERO
        val result = IntArray(trigCheckedSize(words.size.toLong() + 3L))
        if (value < BASE) {
            var carry = 0
            for (i in words.indices) {
                val product = words[i] * value + carry
                result[i] = product % BASE
                carry = product / BASE
            }
            result[words.size] = carry
            return normalized(sign, result)
        }
        var carry = 0L
        for (i in words.indices) {
            val product = words[i].toLong() * value + carry
            result[i] = (product % BASE).toInt()
            carry = product / BASE
        }
        var i = words.size
        while (carry != 0L) {
            result[i++] = (carry % BASE).toInt()
            carry /= BASE
        }
        return normalized(sign, result)
    }

    /** Truncates toward zero. The remainder is an unsigned magnitude. */
    fun divideSmall(value: Int): Pair<TrigInteger, Int> {
        require(value > 0)
        val result = IntArray(words.size)
        if (value <= Int.MAX_VALUE / BASE) {
            var remainder = 0
            for (i in words.lastIndex downTo 0) {
                val current = remainder * BASE + words[i]
                result[i] = current / value
                remainder = current % value
            }
            return normalized(sign, result) to remainder
        }
        var remainder = 0L
        for (i in words.lastIndex downTo 0) {
            val current = remainder * BASE + words[i]
            result[i] = (current / value).toInt()
            remainder = current % value
        }
        return normalized(sign, result) to remainder.toInt()
    }

    /** Positive Euclidean division; neither operand is mutated. */
    private fun positiveDivRem(divisor: TrigInteger): Pair<TrigInteger, TrigInteger> {
        require(sign >= 0 && divisor.sign > 0)
        if (this < divisor) return ZERO to this
        if (divisor.words.size == 1) {
            val (quotient, remainder) = divideSmall(divisor.words[0])
            return quotient to of(remainder)
        }
        val quotient = IntArray(words.size)
        var remainder = ZERO
        for (i in words.lastIndex downTo 0) {
            val shifted = IntArray(trigCheckedSize(remainder.words.size.toLong() + 1L))
            shifted[0] = words[i]
            remainder.words.copyInto(shifted, destinationOffset = 1)
            remainder = normalized(1, shifted)
            var low = 0
            var high = BASE - 1
            while (low < high) {
                val middle = (low + high + 1) / 2
                if (divisor.timesSmall(middle) <= remainder) low = middle else high = middle - 1
            }
            quotient[i] = low
            if (low != 0) remainder -= divisor.timesSmall(low)
        }
        return normalized(1, quotient) to remainder
    }

    fun floorDivide(divisor: TrigInteger): TrigInteger {
        require(divisor.sign > 0)
        val (quotient, remainder) = abs().positiveDivRem(divisor)
        return if (sign < 0) -quotient - if (remainder.isZero) ZERO else ONE else quotient
    }

    fun ceilDivide(divisor: TrigInteger): TrigInteger = -((-this).floorDivide(divisor))

    fun shiftDecimal(places: Int): TrigInteger {
        require(places >= 0)
        if (isZero) return this
        val groups = places / DIGITS_PER_WORD
        val result = IntArray(trigCheckedSize(words.size.toLong() + groups))
        words.copyInto(result, destinationOffset = groups)
        return normalized(sign, result).timesSmall(POWERS_OF_TEN[places % DIGITS_PER_WORD])
    }

    /** Quotient truncated toward zero and whether any nonzero digits were discarded. */
    fun dropDecimal(places: Int): Pair<TrigInteger, Boolean> {
        require(places >= 0)
        if (places == 0 || isZero) return this to false
        val groups = places / DIGITS_PER_WORD
        if (groups >= words.size) return ZERO to true
        val discarded = (0 until groups).any { words[it] != 0 }
        val shortened = normalized(sign, words.copyOfRange(groups, words.size))
        val (quotient, remainder) = shortened.divideSmall(POWERS_OF_TEN[places % DIGITS_PER_WORD])
        return quotient to (discarded || remainder != 0)
    }

    fun floorDropDecimal(places: Int): TrigInteger {
        val (quotient, discarded) = dropDecimal(places)
        return if (sign < 0 && discarded) quotient - ONE else quotient
    }

    fun ceilDropDecimal(places: Int): TrigInteger = -((-this).floorDropDecimal(places))

    fun floorDivideSmall(value: Int): TrigInteger {
        val (quotient, remainder) = divideSmall(value)
        return if (sign < 0 && remainder != 0) quotient - ONE else quotient
    }

    fun ceilDivideSmall(value: Int): TrigInteger = -((-this).floorDivideSmall(value))

    fun squareRootFloor(): TrigInteger {
        require(sign >= 0)
        if (isZero) return ZERO
        val length = magnitudeDigits().length
        var estimate = powerOfTen(length / 2 + length % 2)
        while (true) {
            val next = (estimate + floorDivide(estimate)).divideSmall(2).first
            if (next >= estimate) return estimate
            estimate = next
        }
    }

    fun magnitudeDigits(): String {
        if (isZero) return "0"
        return buildString {
            append(words.last())
            for (i in words.lastIndex - 1 downTo 0) append(words[i].toString().padStart(DIGITS_PER_WORD, '0'))
        }
    }

    fun toDecimalString(scale: Int): String {
        require(scale >= 0)
        val digits = magnitudeDigits().padStart(trigCheckedSize(scale.toLong() + 1L), '0')
        val prefix = if (sign < 0) "-" else ""
        return if (scale == 0) prefix + digits else prefix + digits.dropLast(scale) + "." + digits.takeLast(scale)
    }

    companion object {
        private const val BASE = 10_000
        private const val DIGITS_PER_WORD = 4
        private val POWERS_OF_TEN = intArrayOf(1, 10, 100, 1000)
        val ZERO = TrigInteger(0, IntArray(0))
        val ONE = TrigInteger(1, intArrayOf(1))

        fun of(value: Int): TrigInteger {
            require(value >= 0)
            return parseMagnitude(value.toString())
        }

        fun parseMagnitude(digits: String): TrigInteger {
            require(digits.isNotEmpty() && digits.all { it in '0'..'9' })
            val result = IntArray(1 + (digits.length - 1) / DIGITS_PER_WORD)
            var end = digits.length
            var index = 0
            while (end > 0) {
                val start = maxOf(0, end - DIGITS_PER_WORD)
                result[index++] = digits.substring(start, end).toInt()
                end = start
            }
            return normalized(1, result)
        }

        fun powerOfTen(exponent: Int): TrigInteger {
            require(exponent >= 0)
            val words = IntArray(exponent / DIGITS_PER_WORD + 1)
            words[words.lastIndex] = POWERS_OF_TEN[exponent % DIGITS_PER_WORD]
            return TrigInteger(1, words)
        }

        private fun normalized(sign: Int, source: IntArray): TrigInteger {
            var size = source.size
            while (size > 0 && source[size - 1] == 0) size--
            return if (size == 0) ZERO else TrigInteger(sign, if (size == source.size) source else source.copyOf(size))
        }
    }
}
