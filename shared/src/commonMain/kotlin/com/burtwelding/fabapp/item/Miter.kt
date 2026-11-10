package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.storage.ItemHandle
import deci.trig.atan
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants

@Serializable
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
        val leftMiterAngle: Angle,
        val leftMiterRise: Length,
        val leftMiterSide: MiterAngleSide,
        val rightMiterAngle: Angle,
        val rightMiterRise: Length,
        val rightMiterSide: MiterAngleSide
    )
    fun calculate (): MiterCalculation {
        val biggerPiece = if (leftPieceSize.item.baseUnitDecimalValue > rightPieceSize.item.baseUnitDecimalValue) PieceSide.LEFT else PieceSide.RIGHT
        val biggerPieceSize = if (biggerPiece == PieceSide.LEFT) leftPieceSize.item.baseUnitDecimalValue else rightPieceSize.item.baseUnitDecimalValue
        val smallerPiece = if (biggerPiece == PieceSide.LEFT) PieceSide.RIGHT else PieceSide.LEFT
        val smallerPieceSize = if (smallerPiece == PieceSide.LEFT) leftPieceSize.item.baseUnitDecimalValue else rightPieceSize.item.baseUnitDecimalValue
        val smallerPieceMiterAngle: Angle
        val biggerPieceMiterAngle: Angle
        val smallPieceHypotheticalDiagonal =
            if (totalJointAngle.item.baseUnitDecimalValue > Deci(90))
                smallerPieceSize.div(cos(DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue))))
            else Deci(180)
        val flipBiggerPieceMiter = smallPieceHypotheticalDiagonal < biggerPieceSize
        if (flipBiggerPieceMiter) {
            biggerPieceMiterAngle = Angle(
                convertRadiansToDegrees(
                    (
                            (DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue))
                                .cos()
                                .minus(smallerPieceSize.div(biggerPieceSize))
                                    ).div(
                                    DeciConstants.PI.minus(
                                        convertDegreesToRadians(
                                            totalJointAngle.item.baseUnitDecimalValue
                                        )
                                    ).sin()
                                )
                            ).atan()
                )
            )
            smallerPieceMiterAngle = Angle(
                convertRadiansToDegrees(DeciConstants.PI)
                    .minus(totalJointAngle.item.baseUnitDecimalValue)
                    .plus(biggerPieceMiterAngle.baseUnitDecimalValue)
            )
        } else {
            biggerPieceMiterAngle = Angle(
                convertRadiansToDegrees(
                    (
                            (convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue).minus(
                                DeciConstants.PI
                            ).cos()
                                .minus(smallerPieceSize.div(biggerPieceSize))
                                    ).div(
                                    convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue).minus(
                                        DeciConstants.PI
                                    ).sin()
                                )
                            ).atan()
                )
            )
            smallerPieceMiterAngle = Angle(
                convertRadiansToDegrees(DeciConstants.PI)
                    .minus(totalJointAngle.item.baseUnitDecimalValue)
                    .minus(biggerPieceMiterAngle.baseUnitDecimalValue)
            )
        }
        val smallerPieceMiterRise = Length(
            run {
                val angle = convertDegreesToRadians(smallerPieceMiterAngle.baseUnitDecimalValue)
                angle.sin().div(angle.cos()).times(smallerPieceSize)
            }
        )
        val biggerPieceMiterRise = Length(
            run {
                val angle = convertDegreesToRadians(biggerPieceMiterAngle.baseUnitDecimalValue)
                angle.sin().div(angle.cos()).times(biggerPieceSize)
            }
        )
        return MiterCalculation(
            leftMiterAngle = if (biggerPiece == PieceSide.LEFT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            leftMiterRise = if (biggerPiece == PieceSide.LEFT) biggerPieceMiterRise else smallerPieceMiterRise,
            leftMiterSide = if (biggerPiece == PieceSide.LEFT && flipBiggerPieceMiter) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE,
            rightMiterAngle = if (biggerPiece == PieceSide.RIGHT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            rightMiterRise = if (biggerPiece == PieceSide.RIGHT) biggerPieceMiterRise else smallerPieceMiterRise,
            rightMiterSide = if (biggerPiece == PieceSide.RIGHT && flipBiggerPieceMiter) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE
        )
    }
}