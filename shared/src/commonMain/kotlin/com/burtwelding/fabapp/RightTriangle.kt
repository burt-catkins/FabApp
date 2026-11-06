package com.burtwelding.fabapp

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

val t = RightTriangle (
    operand1 = RightTriangle.Operand.Run(ItemHandle(Length(Deci(1)))),
    operand2 = RightTriangle.Operand.Rise(ItemHandle(Length(Deci(1)))),
)

class RightTriangle(
    private val operand1: Operand,
    private val operand2: Operand,
) : Item() {

    sealed interface Operand {
        class Rise(val handle: ItemHandle<Length>) : Operand
        class Run(val handle: ItemHandle<Length>) : Operand
        class Diagonal(val handle: ItemHandle<Length>) : Operand
        class TopAngle(val handle: ItemHandle<Angle>) : Operand
        class BottomAngle(val handle: ItemHandle<Angle>) : Operand
    }

    init {
        require(operand1::class != operand2::class) { "Both operands are the same type" }
    }

    fun getRise() = this.calculation[Operand.Rise::class]
    fun getRun() = this.calculation[Operand.Run::class]
    fun getDiagonal() = this.calculation[Operand.Diagonal::class]
    fun getTopAngle() = this.calculation[Operand.TopAngle::class]
    fun getBottomAngle() = this.calculation[Operand.BottomAngle::class]

    fun calculate(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?) {
        require(firstOperand::class != secondOperand::class) { "Both operands are the same type" }

        this.calculation = mapOf(
            Operand.Rise::class to getRise(firstOperand, secondOperand, lengthUnitTypeWanted),
            Operand.Run::class to getRun(firstOperand, secondOperand, lengthUnitTypeWanted),
            Operand.Diagonal::class to getDiagonal(firstOperand, secondOperand, lengthUnitTypeWanted),
            Operand.TopAngle::class to getTopAngle(firstOperand, secondOperand, lengthUnitTypeWanted),
            Operand.BottomAngle::class to getBottomAngle(firstOperand, secondOperand, lengthUnitTypeWanted),
        )
    }

    fun getRise(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Length? {
        if (secondOperand is Operand.Rise) return secondOperand.handle.value

        return when (firstOperand) {
            is Operand.Rise -> firstOperand.handle.value
            is Operand.Run -> if (secondOperand is Operand.Diagonal) {
                riseFrom(firstOperand, secondOperand, lengthUnitTypeWanted)
            } else {
                null
            }
            is Operand.Diagonal -> if (secondOperand is Operand.Run) {
                riseFrom(secondOperand, firstOperand, lengthUnitTypeWanted)
            } else {
                null
            }
            is Operand.TopAngle, is Operand.BottomAngle -> null
        }
    }

    private fun riseFrom(run: Operand.Run, diagonal: Operand.Diagonal, displayUnits: LengthUnitType?): Length? {
        val runValue = run.handle.value?.baseUnitDecimalValue ?: return null
        val diagonalValue = diagonal.handle.value?.baseUnitDecimalValue ?: return null
        val squaredRise = (diagonalValue * diagonalValue - runValue * runValue).toDouble()
        return if (squaredRise >= 0) Length(Deci(sqrt(squaredRise)), displayUnits) else null
    }

    fun getRun(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Length? =
        when {
            firstOperand is Operand.Run -> firstOperand.handle.value
            secondOperand is Operand.Run -> secondOperand.handle.value
            else -> null
        }

    fun getDiagonal(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Length? =
        when {
            firstOperand is Operand.Diagonal -> firstOperand.handle.value
            secondOperand is Operand.Diagonal -> secondOperand.handle.value
            else -> null
        }

    fun getTopAngle(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Angle? =
        when {
            firstOperand is Operand.TopAngle -> firstOperand.handle.value
            secondOperand is Operand.TopAngle -> secondOperand.handle.value
            else -> null
        }

    fun getBottomAngle(firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Angle? =
        when {
            firstOperand is Operand.BottomAngle -> firstOperand.handle.value
            secondOperand is Operand.BottomAngle -> secondOperand.handle.value
            else -> null
        }

    constructor (givenRise: Rise, givenRun: Run, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Rise(ItemHandle(givenRise.length)),
        Operand.Run(ItemHandle(givenRun.length)),
    ) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenRun = givenRun,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenRise: Rise, givenRun: Run, lengthUnitTypeWanted: LengthType?) {

    }

    fun recalculate (givenRise: Rise, givenRun: Run, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenRun = givenRun,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    constructor (givenRise: Rise, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Rise(ItemHandle(givenRise.length)),
        Operand.Diagonal(ItemHandle(givenDiagonal.length)),
    ) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenDiagonal = givenDiagonal,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenRise: Rise, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) =
        RightTriangleCalculation(
            rise = givenRise,
            run = Run(
                length = Length(
                    type = lengthUnitTypeWanted,
                    baseUnitDecimalValue = getRun(
                        riseGiven = givenRise,
                        diagonalGiven = givenDiagonal
                    )
                )
            ),
            diagonal = givenDiagonal,
            angle = getAngle(
                riseGiven = givenRise,
                diagonalGiven = givenDiagonal
            )
        )

    fun recalculate (givenRise: Rise, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenDiagonal = givenDiagonal,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    constructor (givenRun: Run, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Run(ItemHandle(givenRun.length)),
        Operand.Diagonal(ItemHandle(givenDiagonal.length)),
    ) {
        this.calculation = calculate(
            givenRun = givenRun,
            givenDiagonal = givenDiagonal,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenRun: Run, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) =
        RightTriangleCalculation(
            rise = Rise(
                Length(
                    baseUnitDecimalValue = getRise(
                        runGiven = givenRun,
                        diagonalGiven = givenDiagonal
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            run = givenRun,
            diagonal = givenDiagonal,
            angle = getAngle(
                runGiven = givenRun,
                diagonalGiven = givenDiagonal
            )

        )

    fun recalculate (givenRun: Run, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenRun = givenRun,
            givenDiagonal = givenDiagonal,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    constructor (givenRise: Rise, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Rise(ItemHandle(givenRise.length)),
        Operand.BottomAngle(ItemHandle(givenAngle)),
    ) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenRise: Rise, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) =
        RightTriangleCalculation(
            rise = givenRise,
            run = Run(
                length = Length(
                    baseUnitDecimalValue = getRun(
                        riseGiven = givenRise,
                        angleGiven = givenAngle
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            diagonal = Diagonal(
                length = Length(
                    baseUnitDecimalValue = getDiagonal(
                        riseGiven = givenRise,
                        angleGiven = givenAngle
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            angle = givenAngle
        )

    fun recalculate (givenRise: Rise, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenRise = givenRise,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    constructor (givenRun: Run, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Run(ItemHandle(givenRun.length)),
        Operand.BottomAngle(ItemHandle(givenAngle)),
    ) {
        this.calculation = calculate(
            givenRun = givenRun,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenRun: Run, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) =
        RightTriangleCalculation(
            rise = Rise(
                length = Length(
                    baseUnitDecimalValue = getRise(
                        runGiven = givenRun,
                        angleGiven = givenAngle,
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            run = givenRun,
            diagonal = Diagonal(
                length = Length(
                    baseUnitDecimalValue = getDiagonal(
                        runGiven = givenRun,
                        angleGiven = givenAngle
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            angle = givenAngle
        )

    fun recalculate (givenRun: Run, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenRun = givenRun,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    constructor (givenDiagonal: Diagonal, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) : this(
        Operand.Diagonal(ItemHandle(givenDiagonal.length)),
        Operand.BottomAngle(ItemHandle(givenAngle)),
    ) {
        this.calculation = calculate(
            givenDiagonal = givenDiagonal,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun calculate (givenDiagonal: Diagonal, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) =
        RightTriangleCalculation(
            rise = Rise(
                length = Length(
                    baseUnitDecimalValue = getRise(
                        diagonalGiven = givenDiagonal,
                        angleGiven = givenAngle
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            run = Run(
                length = Length(
                    baseUnitDecimalValue = getRun(
                        diagonalGiven = givenDiagonal,
                        angleGiven = givenAngle
                    ),
                    type = lengthUnitTypeWanted
                )
            ),
            diagonal = givenDiagonal,
            angle = givenAngle
        )

    fun recalculate (givenDiagonal: Diagonal, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
        this.calculation = calculate(
            givenDiagonal = givenDiagonal,
            givenAngle = givenAngle,
            lengthUnitTypeWanted = lengthUnitTypeWanted
        )
    }

    private fun getAngle (riseGiven: Rise, runGiven: Run) =
        Angle(convertRadiansToDegrees(Deci(tan(riseGiven.length.value.div(runGiven.length.value).toDouble()))))
    private fun getAngle (riseGiven: Rise, diagonalGiven: Diagonal) =
        Angle(convertRadiansToDegrees(Deci(sin(riseGiven.length.value.div(diagonalGiven.length.value).toDouble()))))
    private fun getAngle (runGiven: Run, diagonalGiven: Diagonal) =
        Angle(convertRadiansToDegrees(Deci(cos(runGiven.length.value.div(diagonalGiven.length.value).toDouble()))))
    private fun getRise (runGiven: Run, diagonalGiven: Diagonal): Deci {
        val runSquared = runGiven.length.value.times(runGiven.length.value)
        val diagonalSquared = diagonalGiven.length.value.times(diagonalGiven.length.value)
        return Deci(sqrt((Deci(3).times(Deci(3))).minus(runSquared).toDouble()))
    }
    private fun getRise (runGiven: Run, angleGiven: Angle) =
        Deci(tan(convertDegreesToRadians(angleGiven.degrees).toDouble())).times(runGiven.length.value)
    private fun getRise (diagonalGiven: Diagonal, angleGiven: Angle) =
        Deci(sin(convertDegreesToRadians(angleGiven.degrees).toDouble())).times(diagonalGiven.length.value)
    private fun getRun (riseGiven: Rise, diagonalGiven: Diagonal): Deci {
        val riseSquared = riseGiven.length.value.times(riseGiven.length.value)
        val diagonalSquared = diagonalGiven.length.value.times(diagonalGiven.length.value)
        return Deci(sqrt(diagonalSquared.minus(riseSquared).toDouble()))
    }
    private fun getRun (riseGiven: Rise, angleGiven: Angle) =
        riseGiven.length.value.div(Deci(tan(convertDegreesToRadians(angleGiven.degrees).toDouble())))
    private fun getRun (diagonalGiven: Diagonal, angleGiven: Angle) =
        Deci(cos(convertDegreesToRadians(angleGiven.degrees).toDouble())).times(diagonalGiven.length.value)
    private fun getDiagonal (riseGiven: Rise, runGiven: Run): Deci {
        val riseSquared = riseGiven.length.value.times(riseGiven.length.value)
        val runSquared = runGiven.length.value.times(runGiven.length.value)
        return Deci(sqrt(riseSquared.plus(runSquared).toDouble()))
    }
    private fun getDiagonal (riseGiven: Rise, angleGiven: Angle) =
        riseGiven.length.value.div(Deci(sin(convertDegreesToRadians(angleGiven.degrees).toDouble())))
    private fun getDiagonal (runGiven: Run, angleGiven: Angle) =
        runGiven.length.value.div(Deci(cos(convertDegreesToRadians(angleGiven.degrees).toDouble())))
}