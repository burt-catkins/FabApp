package com.burtwelding.fabapp

import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants

// How are variables saved? In the cloud? For how long?
//      probably locally and for however long. have a way to delete each one or clear everything
//      maybe even have folders to reference days later? that would be very useful.
// Pi is DeciConstants.PI
object Calculator {
    val savedVariables: MutableMap<String, Deci> = mutableMapOf()
    fun variableNameExists (nameOfVariable: String) = savedVariables.contains(nameOfVariable)
    fun saveVariable (valueToSave: Deci, nameOfVariable: String): SaveVariableResult {
        if (variableNameExists(nameOfVariable)) return SaveVariableResult.ALREADY_EXISTS
        savedVariables[nameOfVariable] = valueToSave
        return SaveVariableResult.SUCCESS
    }
    fun simplifyFraction () {}
    fun convertToImperialFraction (deciValue: RealNumber, maxDenominator: Int): ImperialFractionResult {
    }
}