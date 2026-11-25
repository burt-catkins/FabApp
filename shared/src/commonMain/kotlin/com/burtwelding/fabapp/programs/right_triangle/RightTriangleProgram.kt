package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.burtwelding.fabapp.numpad.Numpad

enum class OperandType (val color: Color) {
    RISE (Color.Red),
    RUN (Color.Yellow),
    DIAGONAL (Color.Blue),
    TOP_ANGLE (Color.Magenta),
    BOTTOM_ANGLE (Color.Green)
}
@Composable
fun RightTriangleProgram () {
    Box (modifier = Modifier.fillMaxSize()) {
        Column {
            Box (modifier = Modifier.weight(1f)) {
                RightTriangleCanvas()
            }
            Box (modifier = Modifier.weight(1.5f)) {
                RightTriangleInfoPanel()
            }
        }
        Numpad(RightTriangleData.numpadData)
    }
}