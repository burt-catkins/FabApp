package com.burtwelding.fabapp.programs.miter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.burtwelding.fabapp.darken
import com.burtwelding.fabapp.gray

@Composable
fun MiterCanvas () {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MiterData.miterPaths?.let { miterPaths ->
            drawPath(
                path = miterPaths.leftMiterTrianglePath,
                color = MiterPieceSide.LEFT.color.darken(0.5f).gray(0.5f)
            )
            drawPath(
                path = miterPaths.leftBoxPath,
                color = MiterPieceSide.LEFT.color.darken(0.5f)
            )
            drawPath(
                path = miterPaths.rightMiterTrianglePath,
                color = MiterPieceSide.RIGHT.color.darken(0.5f).gray(0.5f)
            )
            drawPath(
                path = miterPaths.rightBoxPath,
                color = MiterPieceSide.RIGHT.color.darken(0.5f)
            )
        }
    }
}