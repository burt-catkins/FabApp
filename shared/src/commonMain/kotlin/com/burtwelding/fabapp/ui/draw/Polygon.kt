//package com.burtwelding.fabapp.ui.draw
//
//import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
//import deci.trig.cos
//import deci.trig.sin
//import org.kimplify.deci.Deci
//
//class Polygon (
//    /**
//     * Polygon is built by drawing a path. This assumes the first vector originates from a relative origin (0,0).
//     */
//    var vectorPathList: List<Vector>
//) {
////    fun getPolygonPath (vectorPathList: List<Vector>, origin: Vector): Path {
////        val path = Path()
////        path.moveTo(origin.x.toFloat(), origin.y.toFloat())
////        var currentVector = origin
////        vectorPathList.forEach { vector ->
////            currentVector = Vector(currentVector.x + vector.x, currentVector.y + vector.y)
////            path.lineTo(currentVector.x.toFloat(), currentVector.y.toFloat())
////        }
////        path.close()
////        return path
////    }
//}