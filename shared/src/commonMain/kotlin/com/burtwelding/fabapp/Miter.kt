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
        val smallerPieceAngleTest = Deci(cos(smallerPieceSize.div(biggerPieceSize).toDouble()))
        val smallerPieceMiterAngle: Angle
        val biggerPieceMiterAngle: Angle
        val edgeCase = leftPieceSize.item.baseUnitDecimalValue != rightPieceSize.item.baseUnitDecimalValue && smallerPieceAngleTest.plus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)) > DeciConstants.PI
        if (edgeCase) {
            biggerPieceMiterAngle = Angle(convertRadiansToDegrees(Deci(
                atan(
                    (
                            cos(
                                DeciConstants.PI
                                    .minus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)).toDouble()
                            ).minus(
                                smallerPieceSize.div(
                                    biggerPieceSize
                                ).toDouble()
                            )
                            ).div(
                            sin(
                                DeciConstants.PI
                                    .minus(convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)).toDouble()
                            )
                        )
                )
            )))
            smallerPieceMiterAngle = Angle(
                convertRadiansToDegrees(DeciConstants.PI)
                    .minus(totalJointAngle.item.baseUnitDecimalValue)
                    .plus(biggerPieceMiterAngle.baseUnitDecimalValue)
            )
        } else {
            biggerPieceMiterAngle = Angle(convertRadiansToDegrees(Deci(
                atan(
                        (
                            cos(
                                convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)
                                    .minus(DeciConstants.PI).toDouble()
                            ).minus(
                                smallerPieceSize.div(
                                    biggerPieceSize
                                ).toDouble()
                            )
                        ).div(
                            sin(
                                convertDegreesToRadians(totalJointAngle.item.baseUnitDecimalValue)
                                    .minus(DeciConstants.PI).toDouble()
                            )
                        )
                )
            )))
            smallerPieceMiterAngle = Angle(
                convertRadiansToDegrees(DeciConstants.PI)
                    .minus(totalJointAngle.item.baseUnitDecimalValue)
                    .minus(biggerPieceMiterAngle.baseUnitDecimalValue)
            )
        }
        val smallerPieceMiterRise = Length(Deci(
            tan(
                convertDegreesToRadians(smallerPieceMiterAngle.baseUnitDecimalValue).toDouble()
            ).times(smallerPieceSize.toDouble())
        ))
        val biggerPieceMiterRise = Length(Deci(
            tan(
                convertDegreesToRadians(biggerPieceMiterAngle.baseUnitDecimalValue).toDouble()
            ).times(biggerPieceSize.toDouble()))
        )
        return MiterCalculation(
            leftMiterAngle = if (biggerPiece == PieceSide.LEFT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            leftMiterRise = if (biggerPiece == PieceSide.LEFT) biggerPieceMiterRise else smallerPieceMiterRise,
            leftMiterSide = if (biggerPiece == PieceSide.LEFT && edgeCase) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE,
            rightMiterAngle = if (biggerPiece == PieceSide.RIGHT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            rightMiterRise = if (biggerPiece == PieceSide.RIGHT) biggerPieceMiterRise else smallerPieceMiterRise,
            rightMiterSide = if (biggerPiece == PieceSide.RIGHT && edgeCase) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE
        )
    }
}