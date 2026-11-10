package com.burtwelding.fabapp

import androidx.compose.ui.graphics.Path
import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import deci.trig.cos
import deci.trig.sin
import org.kimplify.deci.Deci
import org.kimplify.deci.extension.toFloat

object ShapeDrawer {
    enum class FlipType {
        HORIZONTALLY,
        VERTICALLY
    }
    enum class RotateType {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
    fun scalePolygon (vectors: List<Vector>, scalar: Deci) =
        vectors.map { vector ->
            Vector(vector.x * scalar, vector.y * scalar)
        }

    fun rotatePolygon (vectors: List<Vector>, type: RotateType, degrees: Deci): List<Vector> {
        val radians = convertDegreesToRadians(degrees)
        return vectors.map { vector ->
            when (type) {
                RotateType.CLOCKWISE -> Vector(
                    x = vector.x * cos(radians) - vector.y * sin(radians),
                    y = vector.x * sin(radians) + vector.y * cos(radians)
                )
                RotateType.COUNTER_CLOCKWISE -> Vector(
                    x = vector.x * cos(radians) + vector.y * sin(radians),
                    y = vector.y * cos(radians) - vector.x * sin(radians)
                )
            }
        }
    }
    fun flipPolygon (vectors: List<Vector>, type: FlipType) =
        vectors.map { vector ->
            when (type) {
                FlipType.HORIZONTALLY -> Vector(-vector.x, vector.y)
                FlipType.VERTICALLY -> Vector(vector.x, -vector.y)
            }
        }
    fun getPolygonPath (vectors: List<Vector>, origin: Vector): Path {
        val path = Path()
        path.moveTo(origin.x.toFloat(), origin.y.toFloat())
        var currentVector = origin
        vectors.forEach { vector ->
            currentVector = Vector(currentVector.x + vector.x, currentVector.y + vector.y)
            path.lineTo(currentVector.x.toFloat(), currentVector.y.toFloat())
        }
        path.close()
        return path
    }
    fun translateVector (vector: Vector, translation: Vector) =
        Vector(vector.x + translation.x, vector.y + translation.y)
    fun translatePolygon (vectors: List<Vector>, translation: Vector) =
        vectors.map { vector ->
            translateVector(vector, translation)
        }
}