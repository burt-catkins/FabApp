package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import deci.operators.div
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci
import org.kimplify.deci.extension.toFloat

sealed interface EZCanvasType {
    fun getBounds (): Bounds?
}
sealed interface Transformation
abstract class EZCanvasVector (x: Deci, y: Deci): Vector(x,y), EZCanvasType {
    override fun getBounds() = Bounds(
        leftMostCoordinate = Deci.ZERO,
        rightMostCoordinate = x,
        bottomMostCoordinate = Deci.ZERO,
        topMostCoordinate = y
    )
}
abstract class EZCanvasArc (radius: Deci, center: Vector, startDegrees: Deci, sweepDegrees: Deci): Arc(radius, center, startDegrees, sweepDegrees), EZCanvasType {
    private fun getLeftmostDegree () =
        if (withinArcRange(Deci(180)))
            Deci(180)
        else run {
            val angle1 = this.startDegrees
            val angle1x =
                if (angle1 < Deci(180))
                    angle1
                else
                    Deci(180) - (angle1 % Deci(180))
            val angle2 = this.startDegrees + this.sweepDegrees
            val angle2x =
                if (angle2 < Deci(180))
                    angle2
                else
                    Deci(180) - (angle2 % Deci(180))
            if (angle1x > angle2x) angle1 else angle2
        }
    private fun getRightmostDegree () =
        if (withinArcRange(Deci(0)))
            Deci(0)
        else run {
            val angle1 = this.startDegrees
            val angle1x =
                if (angle1 < Deci(180))
                    angle1
                else
                    Deci(180) - (angle1 % Deci(180))
            val angle2 = this.startDegrees + this.sweepDegrees
            val angle2x =
                if (angle2 < Deci(180))
                    angle2
                else
                    Deci(180) - (angle2 % Deci(180))
            if (angle1x < angle2x) angle1 else angle2
        }
    private fun getTopMostDegree () =
        if (withinArcRange(Deci(90)))
            Deci(90)
        else run {
            val angle1 = this.startDegrees + Deci(90)
            val angle1y =
                if (angle1 < Deci(180))
                    angle1
                else
                    Deci(180) - (angle1 % Deci(180))
            val angle2 = this.startDegrees + this.sweepDegrees + Deci(90)
            val angle2y =
                if (angle2 < Deci(180))
                    angle2
                else
                    Deci(180) - (angle2 % Deci(180))
            (if (angle1y > angle2y) angle1 else angle2) - Deci(90)
        }
    private fun getBottomMostDegree () =
        if (withinArcRange(Deci(270)))
            Deci(270)
        else run {
            val angle1 = this.startDegrees + Deci(90)
            val angle1y =
                if (angle1 < Deci(180))
                    angle1
                else
                    Deci(180) - (angle1 % Deci(180))
            val angle2 = this.startDegrees + this.sweepDegrees + Deci(90)
            val angle2y =
                if (angle2 < Deci(180))
                    angle2
                else
                    Deci(180) - (angle2 % Deci(180))
            (if (angle1y < angle2y) angle1 else angle2) - Deci(90)
        }
    // TODO: TEST THIS SHIT!!!
    override fun getBounds () = Bounds(
        topMostCoordinate = sin(convertDegreesToRadians(getTopMostDegree())).times(this.radius),
        bottomMostCoordinate = sin(convertDegreesToRadians(getBottomMostDegree())).times(this.radius),
        leftMostCoordinate = cos(convertDegreesToRadians(getLeftmostDegree())).times(this.radius),
        rightMostCoordinate = cos(convertDegreesToRadians(getRightmostDegree())).times(this.radius)
    )
}
data class Rotate (val degrees: Deci, val about: Vector): Transformation
data class Translate (val to: Vector): Transformation
data class Bounds (
    val topMostCoordinate: Deci,
    val bottomMostCoordinate: Deci,
    val leftMostCoordinate: Deci,
    val rightMostCoordinate: Deci
)
abstract class Polygon: EZCanvasType {
    var polygonPath: List<EZCanvasVector>
    var translation = Vector(Deci.ZERO,Deci.ZERO)
    constructor (polygonPath: List<EZCanvasVector>, transformations: List<Transformation>) {
        this.polygonPath = polygonPath
        transformations.forEach { transformation ->
            when (transformation) {
                is Rotate -> this.polygonPath.forEach { vector -> vector.rotate(transformation.degrees, transformation.about) }
                is Translate -> this.translation.translate(transformation.to)
            }
        }
    }
    override fun getBounds(): Bounds? {
        if (this.polygonPath.isEmpty())
            return null
        else {
            var topMostCoordinate: Deci? = null
            var bottomMostCoordinate: Deci? = null
            var leftMostCoordinate: Deci? = null
            var rightMostCoordinate: Deci? = null
            this.polygonPath.forEach { vector ->
                val translatedVector = Vector(vector.x, vector.y)
                translatedVector.translate(this.translation)
                topMostCoordinate = topMostCoordinate?.let { if (it < translatedVector.y) translatedVector.y else it }?: translatedVector.y
                bottomMostCoordinate = bottomMostCoordinate?.let { if (it > translatedVector.y) translatedVector.y else it }?: translatedVector.y
                leftMostCoordinate = leftMostCoordinate?.let { if (it > translatedVector.x) translatedVector.x else it }?: translatedVector.x
                rightMostCoordinate = rightMostCoordinate?.let { if (it < translatedVector.x) translatedVector.x else it}?: translatedVector.x
            }
            return Bounds(
                topMostCoordinate = topMostCoordinate!!,
                bottomMostCoordinate = bottomMostCoordinate!!,
                leftMostCoordinate = leftMostCoordinate!!,
                rightMostCoordinate = rightMostCoordinate!!
            )
        }
    }
}
class SolidPolygon (polygonPath: List<EZCanvasVector>,
                    transformations: List<Transformation>,
                    val color: Color): Polygon(polygonPath, transformations)
class ColorVector (x: Deci, y: Deci, val color: Color): EZCanvasVector(x,y)

class OutlinedPolygon (val colorVectorPath: List<ColorVector>,
                       transformations: List<Transformation>,
                       val thiccness: Dp): Polygon(colorVectorPath, transformations)
class ColorArc (radius: Deci, center: Vector, startDegrees: Deci, sweepDegrees: Deci, val color: Color, val thiccness: Dp): EZCanvasArc(radius, center, startDegrees, sweepDegrees)
sealed interface FitType
enum class AutoFit: FitType {
    ALL
}
/**
 * This is a case where you don't necessarily want to fit everything into the Canvas, like the case of a Miter. Select the key vector that this will be based on and choose a percentage of the smaller part of the canvas size (length/width) that the magnitude of the key vector will be equal to.
 */
data class PercentageFit (
    /**
     * Since you're already drawing stuff with vectors before you feed them into the EZCanvas, just save the key vector and give it here. EZ.
     */
    val keyVector: Vector,
    val percentage: Deci
): FitType
/**
 * All types assume a shared origin. A list of vectors assumes a PATH where the first vector starts at the origin and the rest go tip-to-tail. This was chosen as it is just largely easier to draw things this way. There may be a future change that gives you an option to, instead, base all vectors from the origin. Do be aware that using Scale Transformations is pointless if you are fitting to the canvas.
 */
@Composable
fun EZCanvas (draw: List<EZCanvasType>, fitType: FitType) {
    if (draw.isNotEmpty()) {
        fun getDrawingBounds (): Bounds {
            var topMostCoordinate: Deci? = null
            var bottomMostCoordinate: Deci? = null
            var leftMostCoordinate: Deci? = null
            var rightMostCoordinate: Deci? = null
            draw.forEach { type ->
                type.getBounds()?.let { polygonBounds ->
                    topMostCoordinate =
                        topMostCoordinate?.let { if (polygonBounds.topMostCoordinate > it) polygonBounds.topMostCoordinate else it }
                            ?: polygonBounds.topMostCoordinate
                    bottomMostCoordinate =
                        bottomMostCoordinate?.let { if (polygonBounds.bottomMostCoordinate < it) polygonBounds.bottomMostCoordinate else it }
                            ?: polygonBounds.bottomMostCoordinate
                    leftMostCoordinate =
                        leftMostCoordinate?.let { if (polygonBounds.leftMostCoordinate < it) polygonBounds.leftMostCoordinate else it }
                            ?: polygonBounds.leftMostCoordinate
                    rightMostCoordinate =
                        rightMostCoordinate?.let { if (polygonBounds.rightMostCoordinate > it) polygonBounds.rightMostCoordinate else it }
                            ?: polygonBounds.rightMostCoordinate
                }
            }
            return Bounds(
                leftMostCoordinate = leftMostCoordinate!!,
                rightMostCoordinate = rightMostCoordinate!!,
                topMostCoordinate = topMostCoordinate!!,
                bottomMostCoordinate = bottomMostCoordinate!!
            )
        }
        Canvas (
            modifier = Modifier.fillMaxSize()
        ) {
            val scalar: Deci
            val canvasSlope = Deci(size.height.toDouble()) / Deci(size.width.toDouble())
            val totalDrawingBounds = getDrawingBounds()
            val totalDrawingBoundsHeight = totalDrawingBounds.topMostCoordinate - totalDrawingBounds.bottomMostCoordinate
            val totalDrawingBoundsWidth = totalDrawingBounds.rightMostCoordinate - totalDrawingBounds.leftMostCoordinate
            // Get scalar
            when (fitType) {
                AutoFit.ALL -> {
                    val totalDrawingBoundsSlope = totalDrawingBoundsHeight / totalDrawingBoundsWidth
                    scalar =
                        // Wide drawing x skinny canvas
                        if (canvasSlope > totalDrawingBoundsSlope)
                            Deci(size.width.toDouble()) / totalDrawingBoundsWidth
                        // Skinny drawing x wide canvas
                        else
                            Deci(size.height.toDouble()) / totalDrawingBoundsHeight
                }
                is PercentageFit -> {
                    scalar =
                        if (canvasSlope > Deci.ONE)
                            size.width / fitType.keyVector.magnitude() * (fitType.percentage / 100)
                        else
                            size.height / fitType.keyVector.magnitude() * (fitType.percentage / 100)
                }
            }
            // Scale
            draw.forEach { type ->
                when (type) {
                    is Polygon -> {
                        type.polygonPath.forEach { vector -> vector.scale(scalar) }
                        type.translation.scale(scalar)
                    }
                    is Arc -> type.center.scale(scalar)
                    else -> {}
                }
            }
            // Translate center of drawings to center of canvas
            val drawingCenter = Vector(
                totalDrawingBounds.rightMostCoordinate.minus(totalDrawingBounds.leftMostCoordinate).div(Deci(2)),
                totalDrawingBounds.topMostCoordinate.minus(totalDrawingBounds.bottomMostCoordinate).div(Deci(2))
            )
            val centerTranslation = Vector(
                Deci(center.x.toDouble()).minus(drawingCenter.x),
                Deci(center.y.toDouble()).minus(drawingCenter.y)
            )
            draw.forEach { type ->
                when (type) {
                    is Polygon -> {
                        type.polygonPath.forEach { vector -> vector.translate(centerTranslation) }
                        type.translation.translate(centerTranslation)
                    }
                    is Arc -> type.center.translate(centerTranslation)
                    else -> {}
                }
            }
            // Flip drawings about the y of the centers
            draw.forEach { type ->
                when (type) {
                    is Polygon -> {
                        type.polygonPath.forEach { vector -> vector.translate(Vector(Deci.ZERO, vector.y - (vector.y - drawingCenter.y) * Deci(2) )) }
                        type.translation.translate(Vector(Deci.ZERO, type.translation.y - (type.translation.y - drawingCenter.y) * Deci(2)))
                    }
                    is Arc -> type.center.translate(Vector(Deci.ZERO, type.center.y - (type.center.y - drawingCenter.y) * Deci(2)))
                    else -> {}
                }
            }
            // Draw
            draw.forEach { type ->
                when (type) {
                    is ColorArc -> drawArc(
                        color = type.color,
                        startAngle = -type.startDegrees.toFloat(),
                        sweepAngle = -type.sweepDegrees.toFloat(),
                        useCenter = false,
                        topLeft = Offset(
                            type.center.x.toFloat() + type.radius.toFloat(),
                            type.center.y.toFloat() + type.radius.toFloat()
                        ),
                        size = Size(
                            type.radius.toFloat() * 2,
                            type.radius.toFloat() * 2
                        ),
                        style = Stroke(type.thiccness.toPx())
                    )
                    is OutlinedPolygon -> {
                        val origin = Vector(type.translation.x, type.translation.y)
                        type.colorVectorPath.forEach { vector ->
                            drawLine(
                                color = vector.color,
                                start = Offset(origin.x.toFloat(), origin.y.toFloat()),
                                end = Offset(vector.x.toFloat(), vector.y.toFloat()),
                                strokeWidth = type.thiccness.toPx()
                            )
                            origin.translate(vector)
                        }
                    }
                    is SolidPolygon -> {
                        val path = Path()
                        val currentCoordinate = Vector(type.translation.x, type.translation.y)
                        path.moveTo(currentCoordinate.x.toFloat(), currentCoordinate.y.toFloat())
                        type.polygonPath.forEach { vector ->
                            currentCoordinate.translate(vector)
                            path.lineTo(currentCoordinate.x.toFloat(), currentCoordinate.y.toFloat())
                        }
                        path.close()
                        drawPath(
                            path = path,
                            color = type.color
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}