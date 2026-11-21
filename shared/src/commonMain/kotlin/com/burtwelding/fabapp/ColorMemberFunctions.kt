package com.burtwelding.fabapp

import androidx.compose.ui.graphics.Color

fun Color.darken (scalar: Float = 0.5f) = Color(
    red = red * scalar,
    green = green * scalar,
    blue = blue * scalar,
    alpha = alpha
)