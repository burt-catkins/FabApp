package com.burtwelding.fabapp

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class RightTriangle (private val operand1: Operand,
                     private val operand2: Operand): Item() {

    sealed interface Operand {
        data class Rise (val handle: ItemHandle<Length>): Operand
        data class Run (val handle: ItemHandle<Length>): Operand
        data class Diagonal (val handle: ItemHandle<Length>): Operand
        data class TopAngle (val handle: ItemHandle<Angle>): Operand
        data class BottomAngle (val handle: ItemHandle<Angle>): Operand
    }

    init {
        require(operand1::class != operand2::class) { "Both operands are the same type! THE FUHHHHH" }
    }

    fun calculate () =
        mapOf(
            Operand.Rise::class to getRise(),
            Operand.Run::class to getRun(),
            Operand.Diagonal::class to getDiagonal(),
            Operand.TopAngle::class to getTopAngle(),
            Operand.BottomAngle::class to getBottomAngle(),
        )

    fun getRise (): Length {}
    fun getRun (): Length {}
    fun getDiagonal(): Length {}
    fun getTopAngle (): Angle {}
    fun getBottomAngle (): Angle {}

    private fun getAngle (rise: Operand.Rise, run: Operand.Run) =
        Angle(convertRadiansToDegrees(Deci(tan(rise.handle.item.baseUnitDecimalValue.div(run.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getAngle (rise: Operand.Rise, diagonal: Operand.Diagonal) =
        Angle(convertRadiansToDegrees(Deci(sin(rise.handle.item.baseUnitDecimalValue.div(diagonal.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getAngle (run: Operand.Run, diagonal: Operand.Diagonal) =
        Angle(convertRadiansToDegrees(Deci(cos(run.handle.item.baseUnitDecimalValue.div(diagonal.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getRise (run: Operand.Run, diagonal: Operand.Diagonal) =
        Length(Deci(sqrt((diagonal.handle.item.baseUnitDecimalValue.times(diagonal.handle.item.baseUnitDecimalValue)).minus(run.handle.item.baseUnitDecimalValue.times(run.handle.item.baseUnitDecimalValue)).toDouble())))
    private fun getRise (run: Operand.Run, angle: Operand.BottomAngle) =
        Length(Deci(tan(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(run.handle.item.baseUnitDecimalValue))
    private fun getRise (diagonal: Operand.Diagonal, angle: Operand.BottomAngle) =
        Length(Deci(sin(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(diagonal.handle.item.baseUnitDecimalValue))
    private fun getRun (rise: Operand.Rise, diagonal: Operand.Diagonal) =
        Length(Deci(sqrt((rise.handle.item.baseUnitDecimalValue.times(rise.handle.item.baseUnitDecimalValue)).minus(diagonal.handle.item.baseUnitDecimalValue.times(diagonal.handle.item.baseUnitDecimalValue)).toDouble())))
    private fun getRun (rise: Operand.Rise, angle: Operand.BottomAngle) =
        Length(rise.handle.item.baseUnitDecimalValue.div(Deci(tan(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getRun (diagonal: Operand.Diagonal, angle: Operand.BottomAngle) =
        Length(Deci(cos(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(diagonal.handle.item.baseUnitDecimalValue))
    private fun getDiagonal (rise: Operand.Rise, run: Operand.Run) =
        Deci(sqrt((rise.handle.item.baseUnitDecimalValue.times(rise.handle.item.baseUnitDecimalValue)).plus(run.handle.item.baseUnitDecimalValue.times(run.handle.item.baseUnitDecimalValue)).toDouble()))
    private fun getDiagonal (rise: Operand.Rise, angle: Operand.BottomAngle) =
        rise.handle.item.baseUnitDecimalValue.div(Deci(sin(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())))
    private fun getDiagonal (run: Operand.Run, angle: Operand.BottomAngle) =
        run.handle.item.baseUnitDecimalValue.div(Deci(cos(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())))
}