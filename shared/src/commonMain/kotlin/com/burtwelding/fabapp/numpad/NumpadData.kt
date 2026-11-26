package com.burtwelding.fabapp.numpad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.burtwelding.fabapp.number.Number


class NumpadData {
    var currentBufferSelected by mutableStateOf(NumpadBufferType.NUMBER)
    var error by mutableStateOf("")
    var numberNumpadBuffer by mutableStateOf(NumpadBuffer())
    var numeratorNumpadBuffer by mutableStateOf<NumpadBuffer?>(null)
    var denominatorNumpadBuffer by mutableStateOf<NumpadBuffer?>(null)
    var enabled by mutableStateOf(false)
    var numpadOutputFunction by mutableStateOf<(Number?) -> Unit>({})
    var numpadValidateFunction by mutableStateOf<(Number?) -> String>({""})
}