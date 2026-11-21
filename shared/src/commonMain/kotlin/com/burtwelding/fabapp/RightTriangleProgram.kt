package com.burtwelding.fabapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.RightTriangle
import com.burtwelding.fabapp.storage.ItemHandle
import com.burtwelding.fabapp.ui.draw.NumpadInput
import com.burtwelding.fabapp.ui.draw.right_triangle.RightTriangleCanvas
import com.burtwelding.fabapp.ui.draw.right_triangle.RightTriangleInfoPanel
import org.kimplify.deci.Deci

enum class OperandType (val color: Color) {
    RISE (Color.Red.darken(0.7f)),
    RUN (Color.Yellow.darken(0.7f)),
    DIAGONAL (Color.Blue.darken(0.7f)),
    TOP_ANGLE (Color.Magenta.darken(0.7f)),
    BOTTOM_ANGLE (Color.Green.darken(0.7f))
}
class RightTriangleResetter {
    var resetProgram by mutableIntStateOf(0)
}
class RightTriangleState {
    var riseText by mutableStateOf("")
    var runText by mutableStateOf("")
    var diagonalText by mutableStateOf("")
    var bottomAngleText by mutableStateOf("")
    var topAngleText by mutableStateOf("")
    var rightTriangleCalculation by mutableStateOf(
        RightTriangle(
            operand1 = RightTriangle.Operand.Rise(ItemHandle<Length>(Length(Deci.ONE))),
            operand2 = RightTriangle.Operand.Run(ItemHandle<Length>(Length(Deci.ONE))),
        ).calculate()
    )
    val operands by mutableStateOf(
        mutableMapOf<OperandType, RightTriangle.Operand>()
    )
}
@Composable
fun RightTriangleProgram (appState: AppState) {
    val resetter by remember { mutableStateOf(RightTriangleResetter()) }
    key(resetter.resetProgram) { // there is some complicated reason why this is called key, but basically once this key changes, everything in the body starts over
        val programState = RightTriangleState()
        Column (
            modifier = Modifier
                .fillMaxSize()
        ) {
            RightTriangleCanvas(
                programState = programState,
                modifier = Modifier.fillMaxWidth().weight(3f)
            )
            RightTriangleInfoPanel(
                modifier = Modifier.fillMaxWidth().weight(2f),
                resetter = resetter,
                programState = programState,
                appState = appState
            )
        }
    }
}