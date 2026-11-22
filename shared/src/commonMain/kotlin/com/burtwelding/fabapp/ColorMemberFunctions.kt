package com.burtwelding.fabapp

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun Color.darken (darkness: Float = 0.5f) = Color(
    red = this.red * (1f - darkness),
    green = this.green * (1f - darkness),
    blue = this.blue * (1f - darkness),
    alpha = this.alpha,
    colorSpace = this.colorSpace
)

fun Color.gray(grayness: Float = 0.5f): Color {
    val upperBound = max(max(this.red, this.green), this.blue)
    val lowerBound = min(min(this.red, this.green), this.blue)
    val middle = upperBound - ((upperBound - lowerBound) / 2)
    val newRedChange = abs(this.red - middle) * grayness
    val newGreenChange = abs(this.green - middle) * grayness
    val newBlueChange = abs(this.blue - middle) * grayness
    val newRed = if (this.red > middle) this.red - newRedChange else this.red + newRedChange
    val newBlue = if (this.blue > middle) this.blue - newBlueChange else this.blue + newBlueChange
    val newGreen = if (this.green > middle) this.green - newGreenChange else this.green + newGreenChange
    return Color(
        red = newRed,
        green = newGreen,
        blue = newBlue,
        alpha = this.alpha,
        colorSpace = this.colorSpace
    )
}