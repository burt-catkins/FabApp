package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.burtwelding.fabapp.ImperialFraction
import com.burtwelding.fabapp.Number
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.RightTriangle
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci

object RightTriangleData {
    fun reset () {
        operands.clear()
        numbers.clear()
    }
    var maxDenominator by mutableStateOf(ImperialFraction.THIRTY_SECONDS)
    var rightTriangleCalculation by mutableStateOf(
        RightTriangle(
            operand1 = RightTriangle.Operand.Rise(ItemHandle<Length>(Length(Deci.ONE))),
            operand2 = RightTriangle.Operand.Run(ItemHandle<Length>(Length(Deci.ONE))),
        ).calculate()
    )
    val operands = mutableStateMapOf<OperandType, Number>()
    val numbers = mutableStateMapOf<OperandType, Number>()
}