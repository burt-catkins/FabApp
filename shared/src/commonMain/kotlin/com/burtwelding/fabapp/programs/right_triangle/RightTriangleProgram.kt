package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class OperandType (val color: Color) {
    RISE (Color.Red),
    RUN (Color.Yellow),
    DIAGONAL (Color.Blue),
    TOP_ANGLE (Color.Magenta),
    BOTTOM_ANGLE (Color.Green)
}
@Composable
fun RightTriangleProgram () {
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        RightTriangleCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        RightTriangleInfoPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        )
    }
}