package com.burtwelding.fabapp.ui.draw.right_triangle

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.VectorProperty
import com.burtwelding.fabapp.OperandType
import com.burtwelding.fabapp.RightTriangleState
import com.burtwelding.fabapp.item.RightTriangle
import org.kimplify.deci.extension.toFloat
import kotlin.math.max
import kotlin.math.min

@Composable
fun RightTriangleCanvas (
    modifier: Modifier,
    programState: RightTriangleState
) {
    Canvas( modifier = modifier ) {
        val scalar = 0.8f
        val maxLine = min(size.width, size.height) * scalar
        val run = programState.rightTriangleCalculation[OperandType.RUN]?.baseUnitDecimalValue?.toFloat()?: error("man i tried to like draw the run but its fugging null! what da hell!")
        val rise = programState.rightTriangleCalculation[OperandType.RISE]?.baseUnitDecimalValue?.toFloat()?: error("what goin on here man the fugging rise is null! im tryna draw it! bruh!")
        val runScaled = if (run > rise) maxLine else maxLine * run / rise
        val riseScaled = if (rise > run) maxLine else maxLine * rise / run
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
        drawLine( // Run
            OperandType.RUN.color,
            squarePoint,
            bottomPoint,
            7f
        )
        drawLine( // Rise
            OperandType.RISE.color,
            squarePoint,
            topPoint,
            7f
        )
        drawLine( // Diagonal
            OperandType.DIAGONAL.color,
            bottomPoint,
            topPoint,
            7f
        )
        val bottomRadius = runScaled / 3
        val bottomArcPoint = Offset(
            bottomPoint.x - bottomRadius,
            bottomPoint.y - bottomRadius
        )
        val bottomAngle = programState.rightTriangleCalculation[OperandType.BOTTOM_ANGLE]?.baseUnitDecimalValue?.toFloat()?: error("bruh! the bottom angle null! i cant draw that shit! fix it!")
        drawArc(
            color = OperandType.BOTTOM_ANGLE.color,
            startAngle = -180f,
            sweepAngle = bottomAngle,
            useCenter = false,
            topLeft = bottomArcPoint,
            size = Size(bottomRadius * 2, bottomRadius * 2),
            style = Stroke(
                width = 7f
            )
        )
        val topRadius = riseScaled / 3
        val topArcPoint = Offset(
            topPoint.x - topRadius,
            topPoint.y - topRadius
        )
        val topAngle = programState.rightTriangleCalculation[OperandType.TOP_ANGLE]?.baseUnitDecimalValue?.toFloat()?: error("yo what??? the top angle null! how can I draw a top angle arc when its null! what the hell man!")
        drawArc(
            color = OperandType.TOP_ANGLE.color,
            startAngle = 90f,
            sweepAngle = -topAngle,
            useCenter = false,
            topLeft = topArcPoint,
            size = Size(topRadius * 2, topRadius * 2),
            style = Stroke(
                width = 7f
            )
        )
    }
}