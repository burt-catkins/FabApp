package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.Number
import org.kimplify.deci.Deci
import org.kimplify.deci.exception.DeciException

enum class BufferType {
    NUMBER,
    NUMERATOR,
    DENOMINATOR
}

class Buffer {var buffer by mutableStateOf("")}

class NumpadState {
    var currentBufferSelected by mutableStateOf(BufferType.NUMBER)
    var error by mutableStateOf("")
    var numberBuffer by mutableStateOf(Buffer())
    var numeratorBuffer by mutableStateOf<Buffer?>(null)
    var denominatorBuffer by mutableStateOf<Buffer?>(null)
}

@Composable
fun NumpadInput (
    appState: AppState
) {
    val numpadState by remember { mutableStateOf(NumpadState()) }
    @Composable
    fun Tile (
        modifier: Modifier,
        functionWithCurrentBuffer: (Buffer?) -> Unit,
        text: String
    ) {
        Box(
            modifier = modifier
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .clickable {functionWithCurrentBuffer(
                        when (numpadState.currentBufferSelected) {
                            BufferType.NUMBER -> numpadState.numberBuffer
                            BufferType.NUMERATOR -> numpadState.numeratorBuffer
                            BufferType.DENOMINATOR -> numpadState.denominatorBuffer
                        }
                    )}
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = TextUnit(24f, TextUnitType.Sp)
                )
            }
        }
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Error Panel
        if (numpadState.error != "") {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .background(Color.Red)
                    .border(
                        border = BorderStroke(
                            width = 5.dp,
                            color = Color.Black
                        )
                    )
            ) {
                Text(
                    text = numpadState.error,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            }
        }
        // Buffer Panel
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .border(
                    border = BorderStroke(
                        width = 5.dp,
                        color = Color.Black
                    )
                )
        ) {
            NumberText(
                decimal = numpadState.numberBuffer.buffer,
                numerator = numpadState.numeratorBuffer?.buffer,
                denominator = numpadState.denominatorBuffer?.buffer,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // Numpad, top 2
        Row (modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        ) {
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                functionWithCurrentBuffer = {
                    numpadState.numberBuffer = Buffer()
                    numpadState.numeratorBuffer = null
                    numpadState.denominatorBuffer = null
                    numpadState.currentBufferSelected = BufferType.NUMBER
                },
                text = "CLEAR"
            )
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "7" },
                    text = "7"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "4" },
                    text = "4"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "8" },
                    text = "8"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "5" },
                    text = "5"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "9" },
                    text = "9"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "6" },
                    text = "6"
                )
            }
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                functionWithCurrentBuffer = {
                    // First validate if buffers are valid as a group.
                    if (numpadState.numeratorBuffer?.buffer == "" || numpadState.denominatorBuffer?.buffer == "" || (numpadState.numeratorBuffer != null && numpadState.denominatorBuffer == null)) {
                        numpadState.error = "Finish fraction or clear it"
                    } else {
                        try {
                            val number = if (numpadState.numberBuffer.buffer == "") null else Deci(
                                numpadState.numberBuffer.buffer
                            )
                            val numerator = numpadState.numeratorBuffer?.let { Deci(it.buffer) }
                            val denominator = numpadState.denominatorBuffer?.let { Deci(it.buffer) }
                            val fraction = numerator?.let {
                                denominator?.let {
                                    Fraction(
                                        numerator,
                                        denominator
                                    )
                                }
                            }
                            val inputNumber = Number(number, fraction)
                            val inputValidationResult = appState.numpadValidateFunction(inputNumber)
                            if (inputValidationResult == "") {
                                appState.numpadOutputFunction(inputNumber)
                                appState.openNumpad = false
                            } else {
                                numpadState.error = inputValidationResult
                            }
                        } catch (bruh: Fraction.DivideByZero) {
                            numpadState.error = "You cannot divide by zero"
                        } catch (deciProblem: DeciException) {
                            numpadState.error = "Enter a valid number"
                        } catch (otherProblem: Exception) {
                            otherProblem.printStackTrace()
                            numpadState.error = "Something went wrong. Please close numpad and try again."
                        }
                    }
                },
                text = "CONFIRM"
            )
        }
        // Numpad, bottom 2
        Row (modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        ) {
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                functionWithCurrentBuffer = {
                    it?.buffer = it.buffer.dropLast(1)
                },
                text = "DELETE"
            )
            Column (
                modifier = Modifier.fillMaxHeight().weight(2f)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Tile(
                        modifier = Modifier.weight(1f),
                        functionWithCurrentBuffer = { it?.buffer += "1" },
                        text = "1"
                    )
                    Tile(
                        modifier = Modifier.weight(1f),
                        functionWithCurrentBuffer = { it?.buffer += "2" },
                        text = "2"
                    )
                }
                Tile(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "0" },
                    text = "0"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "3" },
                    text = "3"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    functionWithCurrentBuffer = { it?.buffer += "." },
                    text = "."
                )
            }
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                functionWithCurrentBuffer = { currentBuffer ->
                    numpadState.currentBufferSelected = when (numpadState.currentBufferSelected) {
                        BufferType.NUMBER -> BufferType.NUMERATOR
                        BufferType.NUMERATOR -> {
                            if (currentBuffer?.buffer == "")
                                BufferType.NUMBER
                            else BufferType.DENOMINATOR
                        }
                        BufferType.DENOMINATOR -> BufferType.NUMBER
                    }
                    when (numpadState.currentBufferSelected) {
                        BufferType.NUMBER -> {
                            numpadState.numeratorBuffer = null
                            numpadState.denominatorBuffer = null
                        }
                        BufferType.NUMERATOR -> numpadState.numeratorBuffer = Buffer()
                        BufferType.DENOMINATOR -> numpadState.denominatorBuffer = Buffer()
                    }
                },
                text = "FRACTION"
            )
        }
    }
}