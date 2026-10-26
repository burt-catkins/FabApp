package com.burtwelding.fabapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FabApp",
    ) {
        App()
    }
}