package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.burtwelding.fabapp.number.Number
import com.burtwelding.fabapp.numpad.NumpadData
import org.kimplify.deci.Deci

object RightTriangleData {
    fun reset () {
        operands.clear()
        numbers.clear()
    }

    var rightTriangleCalculation by mutableStateOf(
        RightTriangleCalculator(
            operand1 = RightTriangleCalculator.Rise(Deci.ONE),
            operand2 = RightTriangleCalculator.Run(Deci.ONE),
        ).calculation
    )
    val operands = mutableStateMapOf<RightTriangleOperandType, Number>()
    val numbers = mutableStateMapOf<RightTriangleOperandType, Number>()
    val numpadData by mutableStateOf(NumpadData())
}