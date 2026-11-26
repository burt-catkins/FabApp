package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.ui.graphics.Color

enum class RightTriangleOperandType (val color: Color) {
    RISE (Color.Red),
    RUN (Color.Yellow),
    DIAGONAL (Color.Blue),
    TOP_ANGLE (Color.Magenta),
    BOTTOM_ANGLE (Color.Green)
}