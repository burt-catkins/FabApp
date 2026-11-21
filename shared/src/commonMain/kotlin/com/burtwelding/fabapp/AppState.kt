package com.burtwelding.fabapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppState {
    var currentProgram by mutableStateOf(Programs.RIGHT_TRIANGLE)
    var openNumpad by mutableStateOf(false)
    var numpadOutputFunction by mutableStateOf<(String) -> Unit>({})
    var numpadValidateFunction by mutableStateOf<(String) -> String>({""})
}