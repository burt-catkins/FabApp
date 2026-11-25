package com.burtwelding.fabapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object AppState {
    var currentProgram by mutableStateOf(Programs.RIGHT_TRIANGLE)
}