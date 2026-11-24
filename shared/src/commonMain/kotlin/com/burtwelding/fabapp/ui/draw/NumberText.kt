package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.ImperialFraction
import com.burtwelding.fabapp.ImperialFractionalResult
import com.burtwelding.fabapp.Number
import com.burtwelding.fabapp.RoundResult
import deci.trim
import org.kimplify.deci.Deci
import org.kimplify.deci.RoundingMode


@Composable
fun NumberText (decimal: String?,
                numerator: String?,
                denominator: String?,
                modifier: Modifier,
                fontColor: Color,
                fontSizeSP: Int,
                roundResult: RoundResult?) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((fontSizeSP.toFloat() * 0.15).dp)
    ) {
        // Number
        if (decimal != null && (decimal != "0" || numerator == null))
            Text (
                text = decimal,
                fontSize = fontSizeSP.sp,
                color = fontColor
            )
        // Fraction
        if (numerator != null) {
//            Row (
//                modifier = Modifier
//                    .align(Alignment.CenterVertically),
//                horizontalArrangement = Arrangement.spacedBy((fontSizeSP.toFloat() / 3.5).dp)
//            ) {
                Column (
                    modifier = Modifier
                        .width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Numerator
                    Text(
                        text = if (numerator == "") "■" else numerator,
                        fontSize = (fontSizeSP.toFloat() * 2 / 3).sp,
                        color = fontColor
                    )
                    // Fraction line
                    Canvas (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {
                        drawLine(
                            color = fontColor,
                            strokeWidth = 2.dp.toPx(),
                            start = Offset(
                                x = 0f,
                                y = size.height / 2
                            ),
                            end = Offset(
                                x = size.width,
                                y = size.height / 2
                            )
                        )
                    }
                    // Denominator
                    Text(
                        text = denominator?.let{ if (denominator == "") "■" else denominator }?: "□",
                        fontSize = (fontSizeSP.toFloat() * 2 / 3).sp,
                        color = fontColor
                    )
                }
//            }
        }
        if (roundResult != null && roundResult != RoundResult.EXACT)
            Text(
                text = when (roundResult) {
                    RoundResult.UP -> "↑"
                    RoundResult.DOWN -> "↓"
                },
                fontSize = fontSizeSP.sp,
                color = fontColor,
                modifier = Modifier
            )
    }
}

@Composable
fun NumberText (number: Number?,
                modifier: Modifier,
                fontColor: Color,
                fontSizeSP: Int,
                maxDenominator: Deci?,
                decimalPrecision: Int = 3) {
    if (number != null) {
        fun getFractionalNumber (decimal: Deci, denominator: Deci?): ImperialFractionalResult {
            // TODO: Implement unit conversion
//        val inchesDecimal: Deci = when (decimal.displayUnits) {
//            Imperial.INCH, null -> decimal.baseUnitDecimalValue
//            Imperial.FOOT -> decimal.baseUnitDecimalValue.times(Deci(12))
//            Imperial.YARD -> decimal.baseUnitDecimalValue.times(Deci(36))
//            Metric.MILLIMETERS -> convertMetricToInches(decimal).baseUnitDecimalValue
//            Metric.CENTIMETERS -> convertMetricToInches(decimal).baseUnitDecimalValue
//            Metric.METERS -> convertMetricToInches(decimal).baseUnitDecimalValue
//        }
            if (denominator == null) return ImperialFractionalResult(
                inches = decimal,
                fraction = null,
                roundResult = RoundResult.EXACT
            )
            val wholeInches = decimal.setScale(0, RoundingMode.DOWN)
            val remainder = decimal.minus(wholeInches)
            val numeratorDecimal = remainder.times(denominator)
            val numeratorWhole = numeratorDecimal.setScale(0, RoundingMode.DOWN)
            val numeratorRemainder = numeratorDecimal.minus(numeratorWhole)
            val roundResult = when {
                numeratorRemainder.isZero() -> RoundResult.EXACT
                numeratorRemainder >= Deci("0.5") -> RoundResult.UP
                else -> RoundResult.DOWN
            }
            val roundedNumerator = numeratorDecimal.setScale(0, RoundingMode.HALF_UP)
            val roundedNumeratorRoundedToWholeNumber = roundedNumerator == denominator
            val roundedNumeratorPotentiallyWrapped = if (roundedNumeratorRoundedToWholeNumber) Deci.ZERO else roundedNumerator
            return ImperialFractionalResult(
                inches = wholeInches + if (roundedNumeratorRoundedToWholeNumber) Deci.ONE else Deci.ZERO,
                fraction = if (roundedNumeratorPotentiallyWrapped == Deci.ZERO) null else Fraction(
                    originalNumerator = roundedNumeratorPotentiallyWrapped,
                    originalDenominator = denominator
                ),
                roundResult = roundResult
            )
        }
        if (maxDenominator == null)
            NumberText(
                decimal = number.toDeci().trim(decimalPrecision),
                numerator = null,
                denominator = null,
                modifier = modifier,
                fontColor = fontColor,
                fontSizeSP = fontSizeSP,
                roundResult = null
            )
        else {
            val totalDecimal = number.toDeci()
            val newSimplifiedFraction = getFractionalNumber(
                totalDecimal,
                maxDenominator
            )
            NumberText(
                decimal = newSimplifiedFraction.inches.trim(decimalPrecision),
                numerator = newSimplifiedFraction.fraction?.numerator?.trim(decimalPrecision),
                denominator = newSimplifiedFraction.fraction?.denominator?.trim(decimalPrecision),
                modifier = modifier,
                fontColor = fontColor,
                fontSizeSP = fontSizeSP,
                roundResult = newSimplifiedFraction.roundResult
            )
        }
    }
}