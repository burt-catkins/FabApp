package com.burtwelding.fabapp.programs.miter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.burtwelding.fabapp.numpad.NumpadData
import org.kimplify.deci.Deci

object MiterData {
    val numpadData by mutableStateOf(NumpadData())
    var calculation by mutableStateOf(MiterCalculator(Deci.ONE, Deci.ONE, Deci(90)))
}