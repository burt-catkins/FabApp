package com.burtwelding.fabapp

import org.kimplify.deci.Deci
import org.kimplify.deci.RoundingMode
import org.kimplify.deci.formatting.format

fun Deci.trim(decimalPlaces: Int): String {
    require(decimalPlaces >= 0)
    val text = setScale(decimalPlaces, RoundingMode.HALF_UP).toString()
    return if ('.' in text) {
        text.trimEnd('0').trimEnd('.')
    } else {
        text
    }
}