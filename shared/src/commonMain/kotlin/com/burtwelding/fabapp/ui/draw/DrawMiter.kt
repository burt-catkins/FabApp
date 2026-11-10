package com.burtwelding.fabapp.ui.draw

import androidx.compose.ui.graphics.Path
import com.burtwelding.fabapp.ShapeDrawer
import com.burtwelding.fabapp.Vector
import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.Miter
import com.burtwelding.fabapp.storage.ItemHandle
import deci.operators.div
import deci.operators.times
import org.kimplify.deci.Deci

data class MiterPaths (
    val leftMiterTrianglePath: Path,
    val leftBoxPath: Path,
    val rightMiterTrianglePath: Path,
    val rightBoxPath: Path
)

fun getMiterPaths (leftPieceSizeItem: ItemHandle<Length>,
                   rightPieceSizeItem: ItemHandle<Length>,
                   totalJointAngleItem: ItemHandle<Angle>,
                   miterCalculation: Miter.MiterCalculation,
                   windowWidth: Deci,
                   windowHeight: Deci): MiterPaths {

    val center = Vector(windowWidth / 2, windowHeight / 2)

    // TODO: Move this
    val maxPieceWidth = windowWidth * 0.2

//    // TODO: Move all this stuff to a function. These 3 items are the parameters.
    val leftPieceSize = leftPieceSizeItem.item.baseUnitDecimalValue
    val rightPieceSize = rightPieceSizeItem.item.baseUnitDecimalValue
//
//    // TODO: Create validation of angle between 0.001 -> 179.999 (inclusive)
    val totalJointAngle = totalJointAngleItem.item.baseUnitDecimalValue

    fun drawTriangle (angleSide: Miter.MiterAngleSide, rise: Deci, run: Deci) =
        if (angleSide == Miter.MiterAngleSide.OUTSIDE)
            listOf(
                Vector(0, rise), // Down
                Vector(-run, Deci.ZERO), // Left
            )
        else
            listOf(
                Vector(-run, Deci.ZERO), // Left
                Vector(0.0, -rise) // Up
            )
    val leftMiterTriangle = drawTriangle(
        miterCalculation.leftMiterSide,
        miterCalculation.leftMiterRise.baseUnitDecimalValue,
        leftPieceSize
    )
    val rightMiterTriangle = drawTriangle(
        miterCalculation.rightMiterSide,
        miterCalculation.rightMiterRise.baseUnitDecimalValue,
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
        Vector(0.0, windowHeight/2.5),
        Vector(leftBoxWidth, 0.0),
        Vector(0.0, -windowHeight/2.5)
    )
    val rightBoxScaled = listOf(
        Vector(0.0, windowHeight/2.5),
        Vector(rightBoxWidth, 0.0),
        Vector(0.0,-windowHeight/2.5)
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
        center
    )
    val leftBoxPath = ShapeDrawer.getPolygonPath(
        leftBoxScaled,
        run {
            val xOfTriangleFromCenter = ShapeDrawer.translateVector(
                leftMiterTriangleScaled[0],
                center
            )
            if (miterCalculation.leftMiterSide == Miter.MiterAngleSide.OUTSIDE)
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
        center
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
                center
            )
            if (miterCalculation.rightMiterSide == Miter.MiterAngleSide.OUTSIDE)
                ShapeDrawer.translateVector(
                    xOfTriangleFromCenter,
                    rightMiterTriangleScaledFlippedAndRotated[1]
                )
            else xOfTriangleFromCenter
        }
    )
    return MiterPaths(
        leftMiterTrianglePath = leftMiterTrianglePath,
        leftBoxPath = leftBoxPath,
        rightMiterTrianglePath = rightMiterTrianglePath,
        rightBoxPath = rightBoxPath
    )
}