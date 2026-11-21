package com.burtwelding.fabapp.ui.draw

import androidx.compose.runtime.Composable
import com.burtwelding.fabapp.Number


@Composable
fun NumberText (decimal: String, numerator: String?, denominator: String?) {
    // If numerator is null, don't draw a fraction
    // Else draw the line and if denominator is null, show the square char (until its set to a string)
    // The function assumes that if you don't want a fraction anymore, just set the numerator to null
    // If there is neither a decimal (empty string) nor a fraction, then just do an empty string
    //      The reason this isn't a 0 is that this may be used as a placeholder to await input
    //          We don't want this to be interpreted as an actual input of 0. 0 is 0. Nothing is nothing.
}

@Composable
fun NumberText (number: Number) {
    NumberText(
        decimal = number.decimal.toString(),
        numerator = number.fraction?.numerator?.toString(),
        denominator = number.fraction?.denominator?.toString()
    )
}