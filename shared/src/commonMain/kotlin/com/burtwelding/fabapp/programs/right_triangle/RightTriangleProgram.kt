package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.burtwelding.fabapp.numpad.Numpad
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