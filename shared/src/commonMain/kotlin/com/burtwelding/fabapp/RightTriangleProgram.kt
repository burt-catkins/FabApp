package com.burtwelding.fabapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.RightTriangle
import com.burtwelding.fabapp.storage.ItemHandle
import com.burtwelding.fabapp.ui.draw.right_triangle.RightTriangleCanvas
import com.burtwelding.fabapp.ui.draw.right_triangle.RightTriangleInfoPanel
import org.kimplify.deci.Deci

enum class OperandType (val color: Color) {
    RISE (Color.Red),
    RUN (Color.Yellow),
    DIAGONAL (Color.Blue),
    TOP_ANGLE (Color.Magenta),
    BOTTOM_ANGLE (Color.Green)
}
class RightTrianglePersist {
    var resetProgram by mutableIntStateOf(0)
    var maxDenominator by mutableStateOf(ImperialFraction.THIRTY_SECONDS)
}
class RightTriangleState {
    var rightTriangleCalculation by mutableStateOf(
        RightTriangle(
            operand1 = RightTriangle.Operand.Rise(ItemHandle<Length>(Length(Deci.ONE))),
            operand2 = RightTriangle.Operand.Run(ItemHandle<Length>(Length(Deci.ONE))),
        ).calculate()
    )
}
@Composable
fun RightTriangleProgram () {
    val persist by remember { mutableStateOf(RightTrianglePersist()) }
    key(persist.resetProgram) { // there is some complicated reason why this is called key, but basically once this key changes, everything in the body starts over
        val programState = RightTriangleState()
        Column (
            modifier = Modifier
                .fillMaxSize()
        ) {
            RightTriangleCanvas(
                programState = programState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            RightTriangleInfoPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f),
                programPersist = persist,
                programState = programState
            )
        }
    }
}