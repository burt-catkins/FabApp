package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.sp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.Number
import com.burtwelding.fabapp.RoundResult
import com.burtwelding.fabapp.darken
import fabapp.shared.generated.resources.Res
import fabapp.shared.generated.resources.icon_numpad_backspace
import fabapp.shared.generated.resources.icon_numpad_clear
import fabapp.shared.generated.resources.icon_numpad_confirm
import fabapp.shared.generated.resources.icon_numpad_fraction_clear
import fabapp.shared.generated.resources.icon_numpad_fraction_denominator
import fabapp.shared.generated.resources.icon_numpad_fraction_numerator
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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
        subTile: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .padding(3.dp)
        ) {
            Box(
                modifier = modifier
                    .background(Color.Gray)
            ) {
                subTile()
            }
        }
    }
    fun getCurrentBuffer () = when (numpadState.currentBufferSelected) {
        BufferType.NUMBER -> numpadState.numberBuffer
        BufferType.NUMERATOR -> numpadState.numeratorBuffer
        BufferType.DENOMINATOR -> numpadState.denominatorBuffer
    }
    @Composable
    fun ActionTile (
        modifier: Modifier,
        action: () -> Unit,
        icon: DrawableResource,
        description: String
    ) {
        Tile(
            modifier = modifier,
            subTile = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { action() }
                        .padding(10.dp)
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = description,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        )
    }
    @Composable
    fun NumberTile (
        modifier: Modifier,
        numberChar: Char,
    ) {
        Tile (
            modifier = modifier,
            subTile = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            getCurrentBuffer()?.buffer += numberChar
                            numpadState.error = ""
                        }
                ) {
                    Text(
                        text = "$numberChar",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 40.sp
                    )
                }
            }
        )
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth()
        ) {
            // Error Panel
            if (numpadState.error != "") {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red.darken(0.3f))
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
                        fontSize = 24.sp
                    )
                }
            }
        }
        // Buffer Panel
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray)
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
                modifier = Modifier.align(Alignment.Center),
                fontColor = Color.Black,
                fontSizeSP = 50,
                roundResult = RoundResult.EXACT
            )
        }
        // Numpad, top 2
        Row (modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        ) {
            ActionTile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                action = {
                    numpadState.numberBuffer = Buffer()
                    numpadState.numeratorBuffer = null
                    numpadState.denominatorBuffer = null
                    numpadState.currentBufferSelected = BufferType.NUMBER
                    numpadState.error = ""
                },
                icon = Res.drawable.icon_numpad_clear,
                description = "Clear"
            )
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '7'
                )
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '4'
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '8'
                )
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '5'
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '9'
                )
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '6'
                )
            }
            ActionTile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                action = {
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
                            val inputNumber =
                                if (number == null && fraction == null)
                                    null
                                else
                                    Number(number, fraction)
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
                icon = Res.drawable.icon_numpad_confirm,
                description = "Confirm"
            )
        }
        // Numpad, bottom 2
        Row (modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        ) {
            ActionTile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                action = {
                    val currentBuffer = getCurrentBuffer()
                    currentBuffer?.buffer = currentBuffer.buffer.dropLast(1)
                    numpadState.error = ""
                },
                icon = Res.drawable.icon_numpad_backspace,
                description = "Backspace"
            )
            Column (
                modifier = Modifier.fillMaxHeight().weight(2f)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    NumberTile(
                        modifier = Modifier.weight(1f),
                        numberChar = '1'
                    )
                    NumberTile(
                        modifier = Modifier.weight(1f),
                        numberChar = '2'
                    )
                }
                NumberTile(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    numberChar = '0'
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '3'
                )
                NumberTile(
                    modifier = Modifier.weight(1f),
                    numberChar = '.'
                )
            }
            ActionTile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                action = {
                    numpadState.currentBufferSelected = when (numpadState.currentBufferSelected) {
                        BufferType.NUMBER -> BufferType.NUMERATOR
                        BufferType.NUMERATOR -> {
                            if (getCurrentBuffer()?.buffer == "")
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
                    numpadState.error = ""
                },
                icon = when (numpadState.currentBufferSelected) {
                    BufferType.NUMBER -> Res.drawable.icon_numpad_fraction_numerator
                    BufferType.NUMERATOR -> if (numpadState.numeratorBuffer?.buffer == "") Res.drawable.icon_numpad_fraction_clear else Res.drawable.icon_numpad_fraction_denominator
                    BufferType.DENOMINATOR -> Res.drawable.icon_numpad_fraction_clear
                },
                description = "Fraction"
            )
        }
    }
}