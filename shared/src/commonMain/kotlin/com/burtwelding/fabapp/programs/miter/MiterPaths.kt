package com.burtwelding.fabapp.programs.miter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.burtwelding.fabapp.ui.draw.ShapeDrawer
import com.burtwelding.fabapp.ui.draw.Vector
import deci.operators.div
import deci.operators.times
import org.kimplify.deci.Deci

class MiterPaths {
    val leftMiterTrianglePath: Path
    val leftBoxPath: Path
    val rightMiterTrianglePath: Path
    val rightBoxPath: Path
    constructor (leftPieceSize: Deci,
                 rightPieceSize: Deci,
                 totalJointAngle: Deci,
                 miterCalculation: MiterCalculator.MiterCalculation,
                 canvasSize: Size) {

        val canvasWidth = Deci(canvasSize.width.toDouble())
        val canvasHeight = Deci(canvasSize.height.toDouble())
        val maxPieceWidth = canvasWidth * 0.2
        val canvasCenter = Vector(canvasWidth / 2, canvasHeight / 2)

        fun drawTriangle(
            angleSide: MiterCalculator.MiterAngleSide,
            rise: Deci,
            run: Deci
        ) =
            if (angleSide == MiterCalculator.MiterAngleSide.OUTSIDE)
                listOf(
                    Vector(Deci.ZERO, rise), // Down
                    Vector(-run, Deci.ZERO), // Left
                )
            else
                listOf(
                    Vector(-run, Deci.ZERO), // Left
                    Vector(Deci.ZERO, -rise) // Up
                )

        val leftMiterTriangle = drawTriangle(
            miterCalculation.leftMiterSide,
            miterCalculation.leftMiterRise,
            leftPieceSize
        )
        val rightMiterTriangle = drawTriangle(
            miterCalculation.rightMiterSide,
            miterCalculation.rightMiterRise,
            rightPieceSize
        )
        val smallBoxWidth = maxPieceWidth *
                if (leftPieceSize < rightPieceSize)
                    leftPieceSize / rightPieceSize
                else rightPieceSize / leftPieceSize
        val leftBoxWidth = if (leftPieceSize > rightPieceSize) maxPieceWidth else smallBoxWidth
        val rightBoxWidth = if (rightPieceSize > leftPieceSize) maxPieceWidth else smallBoxWidth
        // Boxes start at the top-left corner, touching the bottom-left of the miter triangle
        val leftBoxScaled = listOf(
            Vector(Deci.ZERO, canvasHeight / 2.5),
            Vector(leftBoxWidth, Deci.ZERO),
            Vector(Deci.ZERO, -canvasHeight / 2.5)
        )
        val rightBoxScaled = listOf(
            Vector(Deci.ZERO, canvasHeight / 2.5),
            Vector(rightBoxWidth, Deci.ZERO),
            Vector(Deci.ZERO, -canvasHeight / 2.5)
        )
        val leftMiterTriangleScaled = ShapeDrawer.scalePolygon(
            leftMiterTriangle,
            leftBoxWidth / leftPieceSize
        )
        val rightMiterTriangleScaled = ShapeDrawer.scalePolygon(
            rightMiterTriangle,
            rightBoxWidth / rightPieceSize
        )
        // Draw paths of left piece
        val leftMiterTrianglePath = ShapeDrawer.getPolygonPath(
            leftMiterTriangleScaled,
            canvasCenter
        )
        val leftBoxPath = ShapeDrawer.getPolygonPath(
            leftBoxScaled,
            run {
                val xOfTriangleFromCenter = ShapeDrawer.translateVector(
                    leftMiterTriangleScaled[0],
                    canvasCenter
                )
                if (miterCalculation.leftMiterSide == MiterCalculator.MiterAngleSide.OUTSIDE)
                    ShapeDrawer.translateVector(
                        xOfTriangleFromCenter,
                        leftMiterTriangleScaled[1]
                    )
                else xOfTriangleFromCenter
            }
        )
        // Flip and rotate right pieces before drawing paths
        val rightMiterTriangleScaledAndFlipped = ShapeDrawer.flipPolygon(
            rightMiterTriangleScaled,
            ShapeDrawer.FlipType.HORIZONTALLY
        )
        val rightMiterTriangleScaledFlippedAndRotated = ShapeDrawer.rotatePolygon(
            rightMiterTriangleScaledAndFlipped,
            ShapeDrawer.RotateType.COUNTER_CLOCKWISE,
            totalJointAngle
        )
        val rightMiterTrianglePath = ShapeDrawer.getPolygonPath(
            rightMiterTriangleScaledFlippedAndRotated,
            canvasCenter
        )
        // This seems strange but it's for redirecting the vectors
        val rightBoxScaledFlipped = ShapeDrawer.flipPolygon(
            rightBoxScaled,
            ShapeDrawer.FlipType.HORIZONTALLY
        )
        val rightBoxScaledFlippedAndRotated = ShapeDrawer.rotatePolygon(
            rightBoxScaledFlipped,
            ShapeDrawer.RotateType.COUNTER_CLOCKWISE,
            totalJointAngle
        )
        val rightBoxPath = ShapeDrawer.getPolygonPath(
            rightBoxScaledFlippedAndRotated,
            run {
                val xOfTriangleFromCenter = ShapeDrawer.translateVector(
                    rightMiterTriangleScaledFlippedAndRotated[0],
                    canvasCenter
                )
                if (miterCalculation.rightMiterSide == MiterCalculator.MiterAngleSide.OUTSIDE)
                    ShapeDrawer.translateVector(
                        xOfTriangleFromCenter,
                        rightMiterTriangleScaledFlippedAndRotated[1]
                    )
                else xOfTriangleFromCenter
            }
        )
        this.leftMiterTrianglePath = leftMiterTrianglePath
        this.leftBoxPath = leftBoxPath
        this.rightMiterTrianglePath = rightMiterTrianglePath
        this.rightBoxPath = rightBoxPath
    }
}