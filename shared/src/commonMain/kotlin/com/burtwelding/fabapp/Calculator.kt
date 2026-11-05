package com.burtwelding.fabapp

import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants
import org.kimplify.deci.RoundingMode

object Calculator {
    val savedItems: MutableMap<String, Item> = mutableMapOf()
    fun itemNameExists (nameOfVariable: String) = savedItems.contains(nameOfVariable)
    fun saveItem (valueToSave: Item, nameOfVariable: String): SaveVariableResult {
        if (itemNameExists(nameOfVariable)) return SaveVariableResult.ALREADY_EXISTS
        savedItems[nameOfVariable] = valueToSave
        return SaveVariableResult.SUCCESS
    }
    fun <U: UnitType> convertUnits (numberToConvert: Number<*>): Number<U> {
        TODO()
    }
    fun convertInchesToMetric (inchesDecimal: Deci, metricLengthTypeDesired: Metric): Length =
        Length(
            displayUnits = metricLengthTypeDesired,
            baseUnitDecimalValue = inchesDecimal.times(when (metricLengthTypeDesired) {
                Metric.MILLIMETERS -> Deci("25.4")
                Metric.CENTIMETERS -> Deci("2.54")
                Metric.METERS -> Deci("0.0254")
            })
        )
    fun convertMetricToInches (metricDecimal: Length): Length =
        Length(
            displayUnits = Imperial.INCH,
            baseUnitDecimalValue = metricDecimal.baseUnitDecimalValue * when (metricDecimal.displayUnits) {
                Metric.MILLIMETERS -> Deci("0.03937007874015748031")
                Metric.CENTIMETERS -> Deci("0.3937007874015748031")
                Metric.METERS -> Deci("39.37007874015748031")
                Imperial.INCH, Imperial.FOOT, Imperial.YARD, null ->
                    throw IllegalArgumentException("bruh aint no metric bruh")
            }
        )
    fun getFractionalInches (length: Length, denominator: ImperialFraction): ImperialFractionalResult {
        val inchesDecimal: Deci = when (length.displayUnits) {
            Imperial.INCH, null -> length.baseUnitDecimalValue
            Imperial.FOOT -> length.baseUnitDecimalValue.times(Deci(12))
            Imperial.YARD -> length.baseUnitDecimalValue.times(Deci(36))
            Metric.MILLIMETERS -> convertMetricToInches(length).baseUnitDecimalValue
            Metric.CENTIMETERS -> convertMetricToInches(length).baseUnitDecimalValue
            Metric.METERS -> convertMetricToInches(length).baseUnitDecimalValue
        }
        val wholeInches = inchesDecimal.setScale(0, RoundingMode.DOWN)
        val remainder = inchesDecimal.minus(wholeInches)
        val numeratorDecimal = remainder.times(denominator.denominator)
        val numeratorWhole = numeratorDecimal.setScale(0, RoundingMode.DOWN)
        val numeratorRemainder = numeratorDecimal.minus(numeratorWhole)
        val roundResult = when {
            numeratorRemainder.isZero() -> RoundResult.EXACT
            numeratorRemainder >= Deci("0.5") -> RoundResult.UP
            else -> RoundResult.DOWN
        }
        val roundedNumerator = numeratorDecimal.setScale(0, RoundingMode.HALF_UP)
        val roundedNumeratorRoundedToWholeNumber = roundedNumerator == denominator.denominator
        val roundedNumeratorPotentiallyWrapped = if (roundedNumeratorRoundedToWholeNumber) Deci.ZERO else roundedNumerator
        return ImperialFractionalResult(
            inches = wholeInches + if (roundedNumeratorRoundedToWholeNumber) Deci.ONE else Deci.ZERO,
            fraction = Fraction(
                originalNumerator = roundedNumeratorPotentiallyWrapped,
                originalDenominator = denominator.denominator
            ),
            roundResult = roundResult
        )
    }
    //fun getTotalWeightInPounds (pieces: List<Piece>, metalType: MetalType) =
    //    pieces.map { piece -> piece.getWeightCubicFeet()?: throw AssertionError("All pieces must have type of metal defined") }.sumDeci()
    fun convertRadiansToDegrees (radianAmount: Deci) =
        radianAmount.times(Deci(180)).div(DeciConstants.PI)
    fun convertDegreesToRadians (degreeAmount: Deci) =
        degreeAmount.times(DeciConstants.PI).div(Deci(180))
}
