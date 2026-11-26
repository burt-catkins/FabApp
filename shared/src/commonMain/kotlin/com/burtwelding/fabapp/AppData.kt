package com.burtwelding.fabapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.burtwelding.fabapp.number.ImperialFraction


object AppData {
    var currentProgram by mutableStateOf(Programs.RIGHT_TRIANGLE)
    var maxDenominator by mutableStateOf(ImperialFraction.THIRTY_SECONDS)
}