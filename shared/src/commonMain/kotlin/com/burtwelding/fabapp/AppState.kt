package com.burtwelding.fabapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object AppState {
    var currentProgram by mutableStateOf(Programs.RIGHT_TRIANGLE)
    var openNumpad by mutableStateOf(false)
//    var rightTriangleState by mutableStateOf()
    var numpadOutputFunction by mutableStateOf<(Number?) -> Unit>({})
    var numpadValidateFunction by mutableStateOf<(Number?) -> String>({""})
}