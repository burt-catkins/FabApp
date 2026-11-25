package com.burtwelding.fabapp.programs.right_triangle

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import deci.trig.acos
import deci.trig.asin
import deci.trig.atan
import deci.trig.cos
import deci.trig.sin
import deci.trig.tan
import org.kimplify.deci.Deci
import org.kimplify.deci.math.sqrt

class RightTriangleCalculator (val operand1: Operand,
                               val operand2: Operand) {

    sealed interface Operand { val value: Deci }
    data class Rise (override val value: Deci): Operand
    data class Run (override val value: Deci): Operand
    data class Diagonal (override val value: Deci): Operand
    data class TopAngle (override val value: Deci): Operand
    data class BottomAngle (override val value: Deci): Operand

    init {
        require(operand1::class != operand2::class) { "Both operands are the same type! THE FUHHHHH" }
        if (operand1::class == TopAngle::class && operand2::class == BottomAngle::class)
            throw IllegalArgumentException("Cant have both Operands be angles! Pointless!")
        if (operand2::class == TopAngle::class && operand1::class == BottomAngle::class)
            throw IllegalArgumentException("bruh both operands are angles! whaaaaat?")
    }

    fun calculate () = OperandType.entries.associateWith { operandType ->
        when (operandType) {
            OperandType.RISE -> getRise()
            OperandType.RUN -> getRun()
            OperandType.DIAGONAL -> getDiagonal()
            OperandType.TOP_ANGLE -> getTopAngle()
            OperandType.BOTTOM_ANGLE -> getBottomAngle()
        }
    }

    private fun getRise (): Deci {
        if (operand1 is Rise) return operand1.value
        if (operand2 is Rise) return operand2.value
        return when (operand1) {
            is Run -> when (operand2) {
                is BottomAngle -> getRise(operand1, operand2)
                is Diagonal -> getRise(operand1, operand2)
                is TopAngle -> getRise(operand1, operand2)
                is Run -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Diagonal -> when (operand2) {
                is Run -> getRise(operand2, operand1)
                is BottomAngle -> getRise(operand1, operand2)
                is TopAngle -> getRise(operand1, operand2)
                is Diagonal -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is TopAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Diagonal -> getRise(operand2, operand1)
                is Run -> getRise(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is BottomAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Diagonal -> getRise(operand2, operand1)
                is Run -> getRise(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getRun (): Deci {
        if (operand1 is Run) return operand1.value
        if (operand2 is Run) return operand2.value
        return when (operand1) {
            is Rise -> when (operand2) {
                is BottomAngle -> getRun(operand1, operand2)
                is Diagonal -> getRun(operand1, operand2)
                is TopAngle -> getRun(operand1, operand2)
                is Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Diagonal -> when (operand2) {
                is Rise -> getRun(operand2, operand1)
                is BottomAngle -> getRun(operand1, operand2)
                is TopAngle -> getRun(operand1, operand2)
                is Diagonal -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is TopAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Diagonal -> getRun(operand2, operand1)
                is Rise -> getRun(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is BottomAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Diagonal -> getRun(operand2, operand1)
                is Rise -> getRun(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getDiagonal(): Deci {
        if (operand1 is Diagonal) return operand1.value
        if (operand2 is Diagonal) return operand2.value
        return when (operand1) {
            is Rise -> when (operand2) {
                is BottomAngle -> getDiagonal(operand1, operand2)
                is Run -> getDiagonal(operand1, operand2)
                is TopAngle -> getDiagonal(operand1, operand2)
                is Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Run -> when (operand2) {
                is Rise -> getDiagonal(operand2, operand1)
                is BottomAngle -> getDiagonal(operand1, operand2)
                is TopAngle -> getDiagonal(operand1, operand2)
                is Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is TopAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Run -> getDiagonal(operand2, operand1)
                is Rise -> getDiagonal(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is BottomAngle -> when (operand2) {
                is BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Run -> getDiagonal(operand2, operand1)
                is Rise -> getDiagonal(operand2, operand1)
                is TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getTopAngle (): Deci {
        if (operand1 is TopAngle) return operand1.value
        if (operand2 is TopAngle) return operand2.value
        return when (operand1) {
            is Rise -> when (operand2) {
                is BottomAngle -> getTopAngle(operand2)
                is Run -> getTopAngle(operand1, operand2)
                is Diagonal -> getTopAngle(operand1, operand2)
                is Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Run -> when (operand2) {
                is Rise -> getTopAngle(operand2, operand1)
                is BottomAngle -> getTopAngle(operand2)
                is Diagonal -> getTopAngle(operand1, operand2)
                is Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Diagonal -> when (operand2) {
                is BottomAngle -> getTopAngle(operand2)
                is Run -> getTopAngle(operand2, operand1)
                is Rise -> getTopAngle(operand2, operand1)
                is Diagonal -> throw IllegalArgumentException("both operands are top angles????")
            }
            is BottomAngle -> getTopAngle(operand1)
        }
    }
    private fun getBottomAngle (): Deci {
        if (operand1 is BottomAngle) return operand1.value
        if (operand2 is BottomAngle) return operand2.value
        return when (operand1) {
            is Rise -> when (operand2) {
                is TopAngle -> getBottomAngle(operand2)
                is Run -> getBottomAngle(operand1, operand2)
                is Diagonal -> getBottomAngle(operand1, operand2)
                is Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Run -> when (operand2) {
                is Rise -> getBottomAngle(operand2, operand1)
                is TopAngle -> getBottomAngle(operand2)
                is Diagonal -> getBottomAngle(operand1, operand2)
                is Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Diagonal -> when (operand2) {
                is TopAngle -> getBottomAngle(operand2)
                is Run -> getBottomAngle(operand2, operand1)
                is Rise -> getBottomAngle(operand2, operand1)
                is Diagonal -> throw IllegalArgumentException("both operands are top angles????")
            }
            is TopAngle -> getBottomAngle(operand1)
        }
    }

    private fun getBottomAngle (topAngle: Operand) = Deci(90).minus(topAngle.value)
    private fun getBottomAngle (rise: Rise, run: Run) = convertRadiansToDegrees(atan(rise.value.div(run.value)))

    private fun getBottomAngle (rise: Rise, diagonal: Diagonal) = convertRadiansToDegrees(asin(rise.value.div(diagonal.value)))
    private fun getBottomAngle (run: Run, diagonal: Diagonal) = convertRadiansToDegrees(acos(run.value.div(diagonal.value)))

    private fun getTopAngle (rise: Rise, run: Run) = Deci(90).minus(getBottomAngle(rise, run))
    private fun getTopAngle (rise: Rise, diagonal: Diagonal) = Deci(90).minus(getBottomAngle(rise, diagonal))
    private fun getTopAngle (run: Run, diagonal: Diagonal) = Deci(90).minus(getBottomAngle(run, diagonal))
    private fun getTopAngle (bottomAngle: BottomAngle) = Deci(90).minus(bottomAngle.value)
    private fun getRise (run: Run, diagonal: Diagonal) = ((diagonal.value.times(diagonal.value)).minus(run.value.times(run.value))).sqrt()

    private fun getRise (run: Run, angle: BottomAngle) = tan(convertDegreesToRadians(angle.value)).times(run.value)

    private fun getRise (run: Run, angle: TopAngle) = tan(convertDegreesToRadians(Deci(90).minus(angle.value))).times(run.value)

    private fun getRise (diagonal: Diagonal, angle: BottomAngle) = sin(convertDegreesToRadians(angle.value)).times(diagonal.value)

    private fun getRise (diagonal: Diagonal, angle: TopAngle) = sin(convertDegreesToRadians(Deci(90).minus(angle.value))).times(diagonal.value)

    private fun getRun (rise: Rise, diagonal: Diagonal) = (diagonal.value.times(diagonal.value).minus(rise.value.times(rise.value))).sqrt()

    private fun getRun (rise: Rise, angle: BottomAngle) = rise.value.div(tan(convertDegreesToRadians(angle.value)))
    private fun getRun (rise: Rise, angle: TopAngle) = rise.value.div(tan(convertDegreesToRadians(Deci(90).minus(angle.value))))

    private fun getRun (diagonal: Diagonal, angle: BottomAngle) = cos(convertDegreesToRadians(angle.value)).times(diagonal.value)
    private fun getRun (diagonal: Diagonal, angle: TopAngle) = cos(convertDegreesToRadians(Deci(90).minus(angle.value))).times(diagonal.value)
    private fun getDiagonal (rise: Rise, run: Run) = (rise.value.times(rise.value)).plus(run.value.times(run.value)).sqrt()
    private fun getDiagonal (rise: Rise, angle: BottomAngle) = rise.value.div(sin(convertDegreesToRadians(angle.value)))
    private fun getDiagonal (rise: Rise, angle: TopAngle) = rise.value.div(sin(convertDegreesToRadians(Deci(90).minus(angle.value))))
    private fun getDiagonal (run: Run, angle: BottomAngle) = run.value.div(cos(convertDegreesToRadians(angle.value)))
    private fun getDiagonal (run: Run, angle: TopAngle) = run.value.div(cos(convertDegreesToRadians(Deci(90).minus(angle.value))))
}