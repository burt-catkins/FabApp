package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.number.Fraction
import com.burtwelding.fabapp.number.Number
import com.burtwelding.fabapp.number.RoundResult
import deci.trim
import org.kimplify.deci.Deci
import org.kimplify.deci.RoundingMode


@Composable
fun NumberText (decimal: String?,
                numerator: String?,
                denominator: String?,
                fontColor: Color,
                roundResult: RoundResult?) {
    var numeratorWidthPx by remember { mutableFloatStateOf(0f) }
    var denominatorWidthPx by remember { mutableFloatStateOf(0f) }
    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row (
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy((maxHeight.value * 0.08).dp)
        ) {
            // Number
            if (decimal != null && (decimal != "0" || numerator == null))
                Text (
                    text = decimal,
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 1,
                    color = fontColor
                )
            // Fraction
            if (numerator != null) {
                Column (
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()
                            val barWidth = maxOf(numeratorWidthPx, denominatorWidthPx)
                            if (barWidth > 0f) {
                                val strokePx = size.height * 0.05f
                                val startX = (size.width - barWidth) / 2f
                                drawLine(
                                    color = fontColor,
                                    start = Offset(startX, size.height / 2f),
                                    end = Offset(startX + barWidth, size.height / 2f),
                                    strokeWidth = strokePx
                                )
                            }
                        }
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box (
                        modifier = Modifier
                            .weight(0.4f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Numerator
                        Text(
                            text = if (numerator == "") "■" else numerator,
                            autoSize = TextAutoSize.StepBased(),
                            maxLines = 1,
                            color = fontColor,
                            textAlign = TextAlign.Center,
                            onTextLayout = { result ->
                                numeratorWidthPx = result.size.width.toFloat()
                            }
                        )
                    }
                    Box (
                        modifier = Modifier
                            .weight(0.4f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Denominator
                        Text(
                            text = denominator?.let{ if (denominator == "") "■" else denominator }?: "□",
                            autoSize = TextAutoSize.StepBased(),
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            color = fontColor,
                            onTextLayout = { result ->
                                denominatorWidthPx = result.size.width.toFloat()
                            }
                        )
                    }
                }
            }
            if (roundResult != null && roundResult != RoundResult.EXACT)
                Text(
                    text = when (roundResult) {
                        RoundResult.UP -> "↑"
                        RoundResult.DOWN -> "↓"
                    },
                    color = fontColor,
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 1
                )
        }
    }
}

@Composable
fun NumberText (number: Number?,
                fontColor: Color,
                maxDenominator: Deci?,
                decimalPrecision: Int = 3) {
    data class ImperialFractionalResult (
        val inches: Deci,
        val fraction: Fraction?,
        val roundResult: RoundResult
    )
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
                fontColor = fontColor,
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
                fontColor = fontColor,
                roundResult = newSimplifiedFraction.roundResult
            )
        }
    }
}