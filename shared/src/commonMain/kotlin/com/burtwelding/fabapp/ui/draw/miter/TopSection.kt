//package com.burtwelding.fabapp.ui.draw.miter
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.safeDrawing
//import androidx.compose.foundation.layout.windowInsetsPadding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.boundsInRoot
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.onSizeChanged
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun TopSection(
//    modifier: Modifier = Modifier.Companion
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .weight(1f)
//            .verticalScroll(rememberScrollState())
//            .padding(8.dp)
//            .windowInsetsPadding(WindowInsets.safeDrawing)
//            .onGloballyPositioned {
//                heightOfTopSection = it.boundsInRoot().height
//            }
//    ) {
//        Canvas(
//            modifier = modifier
//                .fillMaxWidth()
//                .height(500.dp)
//                .onSizeChanged{ newSize ->
//                    canvasSize = newSize
//                }
//        ) {
//            miterPaths?.let { paths ->
//                drawPath(
//                    paths.leftMiterTrianglePath,
//                    Color(red = 1f, green = 0.2f, blue = 0.2f)
//                )
//                drawPath(
//                    paths.leftBoxPath,
//                    Color(red = 1f, green = 0f, blue = 0f)
//                )
//                drawPath(
//                    paths.rightMiterTrianglePath,
//                    Color(blue = 1f, green = 0.2f, red = 0.2f)
//                )
//                drawPath(
//                    paths.rightBoxPath,
//                    Color(blue = 1f, green = 0f, red = 0f)
//                )
//            }
//        }
//
//    }
//}