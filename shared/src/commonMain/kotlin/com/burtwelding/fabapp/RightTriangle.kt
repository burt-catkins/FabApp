package com.burtwelding.fabapp

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.uuid.Uuid

class RightTriangle {

    enum class OperandType {
        RISE,
        RUN,
        DIAGONAL,
        TOP_ANGLE,
        BOTTOM_ANGLE
    }

    class Operand private constructor(
        val type: OperandType,
        private val handle: ItemHandle
    ) {
        constructor(type: OperandType, number: Number<*>) :
                this(type, ItemHandle.direct(number))

        constructor(type: OperandType, key: Uuid) :
                this(type, ItemHandle.saved(key))

        val value: Number<*>?
            get() = handle.value as? Number<*>

        fun save(key: Uuid) = handle.save(key)
    }

    private var operand1: Operand
    private var operand2: Operand

    fun getRise() = this.calculation[OperandType.RISE]
    fun getRun() = this.calculation[OperandType.RUN]
    fun getDiagonal() = this.calculation[OperandType.DIAGONAL]
    fun getTopAngle() = this.calculation[OperandType.TOP_ANGLE]
    fun getBottomAngle() = this.calculation[OperandType.BOTTOM_ANGLE]

    constructor (firstOperand: Operand, secondOperand: Operand) {
        firstOperand.
    }
    }

    fun calculate (firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?) {
        if (firstOperand.type == secondOperand.type) throw IllegalArgumentException("Both Operands are the same type!")
        this.calculation = OperandType.entries.associateWith { operandType ->
            when (operandType) {
                OperandType.RISE -> getRise(firstOperand, secondOperand, lengthUnitTypeWanted)
                OperandType.RUN -> getRun(firstOperand, secondOperand, lengthUnitTypeWanted)
                OperandType.DIAGONAL -> getDiagonal(firstOperand, secondOperand, lengthUnitTypeWanted)
                OperandType.TOP_ANGLE -> getTopAngle(firstOperand, secondOperand, lengthUnitTypeWanted)
                OperandType.BOTTOM_ANGLE -> getBottomAngle(firstOperand, secondOperand, lengthUnitTypeWanted)
            }
        }
    }

    fun getRise (firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Number<UnitType> {
        if (firstOperand.type == OperandType.RISE) return firstOperand.value
        if (secondOperand.type == OperandType.RISE) return secondOperand.value
        when (firstOperand.type) {
            OperandType.RUN -> when (secondOperand.type) {
                OperandType.DIAGONAL -> {
                    val rise = Deci(
                        sqrt(
                            (secondOperand.value.baseUnitDecimalValue.times(secondOperand.value.baseUnitDecimalValue))
                                .minus(firstOperand.value.baseUnitDecimalValue.times(firstOperand.value.baseUnitDecimalValue)).toDouble()
                        )
                    )
                    return Number<>
                }
            }
            OperandType.DIAGONAL -> when (secondOperand.type) { }
            OperandType.TOP_ANGLE -> when (secondOperand.type) { }
            OperandType.BOTTOM_ANGLE -> when (secondOperand.type) {}
    }
    fun getRun (firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Number<UnitType> {
        if (firstOperand.type == OperandType.RUN) return firstOperand.value
        if (secondOperand.type == OperandType.RUN) return secondOperand.value
    }
    fun getDiagonal (firstOperand: Operand, secondOperand: Operand, lengthUnitTypeWanted: LengthUnitType?): Number<UnitType> {
        if (firstOperand.type == OperandType.DIAGONAL) return firstOperand.value
        if (secondOperand.type == OperandType.DIAGONAL) return secondOperand.value
    }
    fun getTopAngle (firstOperand: Operand, secondOperand: Operand): Number<UnitType> {
        if (firstOperand.type == OperandType.TOP_ANGLE) return firstOperand.value
        if (secondOperand.type == OperandType.TOP_ANGLE) return secondOperand.value
    }
    fun getBottomAngle (firstOperand: Operand, secondOperand: Operand): Number<UnitType> {
        if (firstOperand.type == OperandType.BOTTOM_ANGLE) return firstOperand.value
        if (secondOperand.type == OperandType.BOTTOM_ANGLE) return secondOperand.value
    }

    constructor (givenRise: Rise, givenRun: Run, lengthUnitTypeWanted: LengthType?) {
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

    constructor (givenRise: Rise, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) {
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

    constructor (givenRun: Run, givenDiagonal: Diagonal, lengthUnitTypeWanted: LengthType?) {
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

    constructor (givenRise: Rise, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
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

    constructor (givenRun: Run, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
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

    constructor (givenDiagonal: Diagonal, givenAngle: Angle, lengthUnitTypeWanted: LengthType?) {
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