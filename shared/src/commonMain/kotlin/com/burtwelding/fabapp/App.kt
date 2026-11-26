package com.burtwelding.fabapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.burtwelding.fabapp.programs.miter.Miter
import com.burtwelding.fabapp.programs.right_triangle.RightTriangle
import com.burtwelding.fabapp.ui.draw.ProgramPane

@Composable
@Preview
fun App() {
    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val screenHeightPx = constraints.maxHeight
        if (screenHeightPx > 0f) {
            val globalDensity = screenHeightPx / 1280f
            CompositionLocalProvider(
                value = LocalDensity provides Density(
                    density = globalDensity,
                    fontScale = 1f
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ProgramPane(
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .fillMaxHeight()
                                .weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(6f)
                                .background(Color(0.1f, 0.1f, 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clipToBounds()
                            ) {
                                when (AppData.currentProgram) {
                                    Programs.RIGHT_TRIANGLE -> RightTriangle()
                                    Programs.MITER -> Miter()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}