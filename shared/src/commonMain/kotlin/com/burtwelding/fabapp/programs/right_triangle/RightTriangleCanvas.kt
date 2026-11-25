package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.darken
import com.burtwelding.fabapp.gray
import org.kimplify.deci.extension.toFloat
import kotlin.math.min

@Composable
fun RightTriangleCanvas (
    modifier: Modifier
) {
    Canvas (
        modifier = modifier
    ) {
        val run = RightTriangleData.rightTriangleCalculation[OperandType.RUN]?.baseUnitDecimalValue?.toFloat()?: error("man i tried to like draw the run but its fugging null! what da hell!")
        val rise = RightTriangleData.rightTriangleCalculation[OperandType.RISE]?.baseUnitDecimalValue?.toFloat()?: error("what goin on here man the fugging rise is null! im tryna draw it! bruh!")
        val canvasPadding = 0.8f
        val canvasRatio = size.height / size.width
        val triangleRatio = rise / run
        val maxLineIsRise = triangleRatio > canvasRatio
        val maxHeight = size.height * canvasPadding
        val maxWidth = size.width * canvasPadding
        val runScaled =
            if (maxLineIsRise)
                maxWidth * (canvasRatio / triangleRatio)
            else
                maxWidth
        val riseScaled =
            if (maxLineIsRise)
                maxHeight
            else
                maxHeight * (triangleRatio / canvasRatio)
        val squarePoint = Offset(
            center.x - runScaled / 2,
            center.y + riseScaled / 2
        )
        val bottomPoint = Offset(
            center.x + runScaled / 2,
            center.y + riseScaled / 2
        )
        val topPoint = Offset(
            center.x - runScaled / 2,
            center.y - riseScaled / 2
        )
        val topAngle = RightTriangleData.rightTriangleCalculation[OperandType.TOP_ANGLE]?.baseUnitDecimalValue?.toFloat()?: error("yo what??? the top angle null! how can I draw a top angle arc when its null! what the hell man!")
        val bottomAngle = RightTriangleData.rightTriangleCalculation[OperandType.BOTTOM_ANGLE]?.baseUnitDecimalValue?.toFloat()?: error("bruh! the bottom angle null! i cant draw that shit! fix it!")
        val lineThiccnessNormal = 15f
        val lineThiccnessSmallest = 5f
        val lineThiccness = (lineThiccnessSmallest + (lineThiccnessNormal - lineThiccnessSmallest) * (min(topAngle, bottomAngle) / 45.0)).dp.toPx() // (lineThiccnessMinimum + lineThiccnessMaximumScalar * min(topAngle, bottomAngle) / 45).dp.toPx()
        val smallestFractionOfLineForRadius = 5
        val biggestFractionOfLineForRadius = 2
        val bottomRadius = (runScaled / (biggestFractionOfLineForRadius + ((smallestFractionOfLineForRadius - biggestFractionOfLineForRadius) * (min(bottomAngle, topAngle) / 45))))
        val bottomArcPoint = Offset(
            bottomPoint.x - bottomRadius,
            bottomPoint.y - bottomRadius
        )
        val topRadius = (riseScaled / (biggestFractionOfLineForRadius + ((smallestFractionOfLineForRadius - biggestFractionOfLineForRadius) * (min(bottomAngle, topAngle) / 45))))
        val topArcPoint = Offset(
            topPoint.x - topRadius,
            topPoint.y - topRadius
        )
        drawArc(
            color = OperandType.BOTTOM_ANGLE.color.darken(0.25f).gray(0.3f),
            startAngle = -180f,
            sweepAngle = bottomAngle,
            useCenter = false,
            topLeft = bottomArcPoint,
            size = Size(bottomRadius * 2, bottomRadius * 2),
            style = Stroke(lineThiccness)
        )
        drawArc(
            color = OperandType.TOP_ANGLE.color.darken(0.25f).gray(0.3f),
            startAngle = 90f,
            sweepAngle = -topAngle,
            useCenter = false,
            topLeft = topArcPoint,
            size = Size(topRadius * 2, topRadius * 2),
            style = Stroke(lineThiccness)
        )
        drawLine( // Run
            OperandType.RUN.color.darken(0.25f).gray(0.3f),
            squarePoint,
            bottomPoint,
            lineThiccness
        )
        drawLine( // Rise
            OperandType.RISE.color.darken(0.25f).gray(0.3f),
            squarePoint,
            topPoint,
            lineThiccness
        )
        drawLine( // Diagonal
            OperandType.DIAGONAL.color.darken(0.25f).gray(0.3f),
            bottomPoint,
            topPoint,
            lineThiccness
        )
    }
}