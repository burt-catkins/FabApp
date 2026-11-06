package com.burtwelding.fabapp

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Miter (val leftPieceSize: ItemHandle<Length>,
             val rightPieceSize: ItemHandle<Length>,
             val totalJointAngle: ItemHandle<Angle>): Item() {
    enum class PieceSide {
        LEFT,
        RIGHT
    }
    enum class MiterAngleSide {
        INSIDE,
        OUTSIDE
    }
    data class MiterCalculation (
        val miterAngle: Angle,
        val miterAngleSide: MiterAngleSide
    )
    fun calculate (): Map<PieceSide, MiterCalculation> {
        val biggerPiece = if (leftPieceSize.item.baseUnitDecimalValue > rightPieceSize.item.baseUnitDecimalValue) PieceSide.LEFT else PieceSide.RIGHT
        val biggerPieceSize = if (biggerPiece == PieceSide.LEFT) leftPieceSize.item.baseUnitDecimalValue else rightPieceSize.item.baseUnitDecimalValue
        val smallerPiece = if (biggerPiece == PieceSide.LEFT) PieceSide.RIGHT else PieceSide.LEFT
        val smallerPieceSize = if (smallerPiece == PieceSide.LEFT) leftPieceSize.item.baseUnitDecimalValue else rightPieceSize.item.baseUnitDecimalValue
        val smallerPieceAngleTest = Deci(cos(smallerPieceSize.div(biggerPieceSize).toDouble()))
        val smallerPieceMiterAngle: Angle
        val biggerPieceMiterAngle: Angle
        val edgeCase = leftPieceSize.item.baseUnitDecimalValue != rightPieceSize.item.baseUnitDecimalValue && smallerPieceAngleTest.plus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)) > DeciConstants.PI
        if (edgeCase) {
            biggerPieceMiterAngle = Angle(convertRadiansToDegrees(Deci(atan((Deci.ONE.div(Deci(tan(DeciConstants.PI.minus(totalJointAngle.item.baseUnitDecimalValue).toDouble())))).minus(smallerPieceSize.div(biggerPieceSize).times(Deci.ONE.div(Deci(sin(DeciConstants.PI.minus(totalJointAngle.item.baseUnitDecimalValue).toDouble()))))).toDouble()))))
            smallerPieceMiterAngle = Angle(DeciConstants.PI.plus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)).plus(biggerPieceMiterAngle.baseUnitDecimalValue))
        } else {
            biggerPieceMiterAngle = Angle(convertRadiansToDegrees(Deci(atan((Deci.ONE.div(Deci(tan(totalJointAngle.item.baseUnitDecimalValue.minus(DeciConstants.PI).toDouble())))).minus(biggerPieceSize.div(smallerPieceSize).times(Deci.ONE.div(Deci(sin(totalJointAngle.item.baseUnitDecimalValue.minus(DeciConstants.PI).toDouble()))))).toDouble()))))
            smallerPieceMiterAngle = Angle(DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)).plus(biggerPieceMiterAngle.baseUnitDecimalValue))
        }
        fun getMapValue (pieceSide: PieceSide) =
            if (biggerPiece == pieceSide)
                MiterCalculation(
                    miterAngle = biggerPieceMiterAngle,
                    miterAngleSide =
                        if (edgeCase) MiterAngleSide.OUTSIDE
                        else MiterAngleSide.INSIDE
                )
            else MiterCalculation(smallerPieceMiterAngle, MiterAngleSide.INSIDE)
        return mapOf(
            PieceSide.LEFT to getMapValue(PieceSide.LEFT),
            PieceSide.RIGHT to getMapValue(PieceSide.RIGHT)
        )
    }
}