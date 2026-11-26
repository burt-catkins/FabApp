package com.burtwelding.fabapp.programs.miter

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import deci.trig.atan
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants

class MiterCalculator {
    enum class MiterAngleSide {
        INSIDE,
        OUTSIDE
    }
    data class MiterCalculation (
        val leftMiterAngle: Deci,
        val leftMiterRise: Deci,
        val leftMiterSide: MiterAngleSide,
        val rightMiterAngle: Deci,
        val rightMiterRise: Deci,
        val rightMiterSide: MiterAngleSide
    )
    val leftPieceSize: Deci
    val rightPieceSize: Deci
    val totalJointAngle: Deci
    val calculation: MiterCalculation

    constructor (leftPieceSize: Deci, rightPieceSize: Deci, totalJointAngle: Deci) {
        this.leftPieceSize = leftPieceSize
        this.rightPieceSize = rightPieceSize
        this.totalJointAngle = totalJointAngle
        val biggerPiece = if (leftPieceSize > rightPieceSize) MiterPieceSide.LEFT else MiterPieceSide.RIGHT
        val biggerPieceSize = if (biggerPiece == MiterPieceSide.LEFT) leftPieceSize else rightPieceSize
        val smallerPiece = if (biggerPiece == MiterPieceSide.LEFT) MiterPieceSide.RIGHT else MiterPieceSide.LEFT
        val smallerPieceSize = if (smallerPiece == MiterPieceSide.LEFT) leftPieceSize else rightPieceSize
        val smallerPieceMiterAngle: Deci
        val biggerPieceMiterAngle: Deci
        val flipBiggerPieceMiter = totalJointAngle > Deci(90) && smallerPieceSize.div(cos(DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle)))) < biggerPieceSize
        if (flipBiggerPieceMiter) {
            biggerPieceMiterAngle = convertRadiansToDegrees(((DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle)).cos().minus(smallerPieceSize.div(biggerPieceSize))).div(DeciConstants.PI.minus(convertDegreesToRadians(totalJointAngle)).sin())).atan())
            smallerPieceMiterAngle = convertRadiansToDegrees(DeciConstants.PI).minus(totalJointAngle).plus(biggerPieceMiterAngle)

        } else {
            biggerPieceMiterAngle = convertRadiansToDegrees(((convertDegreesToRadians(totalJointAngle).minus(DeciConstants.PI).cos().minus(smallerPieceSize.div(biggerPieceSize))).div(convertDegreesToRadians(totalJointAngle).minus(DeciConstants.PI).sin())).atan())
            smallerPieceMiterAngle = convertRadiansToDegrees(DeciConstants.PI).minus(totalJointAngle).minus(biggerPieceMiterAngle)
        }
        val smallerPieceMiterRise = run {
            val angle = convertDegreesToRadians(smallerPieceMiterAngle)
            angle.sin().div(angle.cos()).times(smallerPieceSize)
        }

        val biggerPieceMiterRise = run {
            val angle = convertDegreesToRadians(biggerPieceMiterAngle)
            angle.sin().div(angle.cos()).times(biggerPieceSize)
        }
        this.calculation = MiterCalculation(
            leftMiterAngle = if (biggerPiece == MiterPieceSide.LEFT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            leftMiterRise = if (biggerPiece == MiterPieceSide.LEFT) biggerPieceMiterRise else smallerPieceMiterRise,
            leftMiterSide = if (biggerPiece == MiterPieceSide.LEFT && flipBiggerPieceMiter) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE,
            rightMiterAngle = if (biggerPiece == MiterPieceSide.RIGHT) biggerPieceMiterAngle else smallerPieceMiterAngle,
            rightMiterRise = if (biggerPiece == MiterPieceSide.RIGHT) biggerPieceMiterRise else smallerPieceMiterRise,
            rightMiterSide = if (biggerPiece == MiterPieceSide.RIGHT && flipBiggerPieceMiter) MiterAngleSide.OUTSIDE else MiterAngleSide.INSIDE
        )
    }
}