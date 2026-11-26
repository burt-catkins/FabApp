package com.burtwelding.fabapp.numpad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.Number
import com.burtwelding.fabapp.RoundResult
import com.burtwelding.fabapp.darken
import com.burtwelding.fabapp.ui.draw.NumberText
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

@Composable
fun Numpad (numpadData: NumpadData) {
    fun getCurrentBuffer () = when (numpadData.currentBufferSelected) {
        NumpadBufferType.NUMBER -> numpadData.numberNumpadBuffer
        NumpadBufferType.NUMERATOR -> numpadData.numeratorNumpadBuffer
        NumpadBufferType.DENOMINATOR -> numpadData.denominatorNumpadBuffer
    }
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
                            numpadData.error = ""
                        }
                ) {
                    Text(
                        text = "$numberChar",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 60.sp
                    )
                }
            }
        )
    }
    fun reset () {
        numpadData.numberNumpadBuffer = NumpadBuffer()
        numpadData.numeratorNumpadBuffer = null
        numpadData.denominatorNumpadBuffer = null
        numpadData.currentBufferSelected = NumpadBufferType.NUMBER
        numpadData.error = ""
    }
    fun close () {
        reset()
        numpadData.enabled = false
    }
    if (numpadData.enabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable( // This just makes it so that nothing can be clicked on the background, since program tiles have spacing
                    enabled = false,
                    onClick = {}
                )
                .background(Color(0.1f, 0.1f, 0.1f, 0.8f))
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { close() }
            ) {
                if (numpadData.error != "") {
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(maxHeight * 0.2f)
                            .background(Color.Red.darken(0.3f))
                            .border(
                                border = BorderStroke(
                                    width = 5.dp,
                                    color = Color.Black
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numpadData.error,
                            autoSize = TextAutoSize.StepBased(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box (
                            modifier = Modifier
                                .fillMaxHeight(0.8f),
                        ) {
                            NumberText(
                                decimal = numpadData.numberNumpadBuffer.buffer,
                                numerator = numpadData.numeratorNumpadBuffer?.buffer,
                                denominator = numpadData.denominatorNumpadBuffer?.buffer,
                                fontColor = Color.Black,
                                roundResult = RoundResult.EXACT
                            )
                        }
                    }
                    // Numpad, top 2
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                    ) {
                        ActionTile(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            action = { reset() },
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
                                if (numpadData.numeratorNumpadBuffer?.buffer == "" || numpadData.denominatorNumpadBuffer?.buffer == "" || (numpadData.numeratorNumpadBuffer != null && numpadData.denominatorNumpadBuffer == null)) {
                                    numpadData.error = "Finish fraction or clear it"
                                } else {
                                    try {
                                        val number = if (numpadData.numberNumpadBuffer.buffer == "") null else Deci(
                                            numpadData.numberNumpadBuffer.buffer
                                        )
                                        val numerator = numpadData.numeratorNumpadBuffer?.let { Deci(it.buffer) }
                                        val denominator = numpadData.denominatorNumpadBuffer?.let { Deci(it.buffer) }
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
                                        val inputValidationResult = numpadData.numpadValidateFunction(inputNumber)
                                        if (inputValidationResult == "") {
                                            numpadData.numpadOutputFunction(inputNumber)
                                            close()
                                        } else {
                                            numpadData.error = inputValidationResult
                                        }
                                    } catch (_: Fraction.DivideByZero) {
                                        numpadData.error = "You cannot divide by zero"
                                    } catch (_: DeciException) {
                                        numpadData.error = "Enter a valid number"
                                    } catch (otherProblem: Exception) {
                                        otherProblem.printStackTrace()
                                        numpadData.error = "Something went wrong. Please close numpad and try again."
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
                                numpadData.error = ""
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
                                numpadData.currentBufferSelected = when (numpadData.currentBufferSelected) {
                                    NumpadBufferType.NUMBER -> NumpadBufferType.NUMERATOR
                                    NumpadBufferType.NUMERATOR -> {
                                        if (getCurrentBuffer()?.buffer == "")
                                            NumpadBufferType.NUMBER
                                        else NumpadBufferType.DENOMINATOR
                                    }
                                    NumpadBufferType.DENOMINATOR -> NumpadBufferType.NUMBER
                                }
                                when (numpadData.currentBufferSelected) {
                                    NumpadBufferType.NUMBER -> {
                                        numpadData.numeratorNumpadBuffer = null
                                        numpadData.denominatorNumpadBuffer = null
                                    }
                                    NumpadBufferType.NUMERATOR -> numpadData.numeratorNumpadBuffer = NumpadBuffer()
                                    NumpadBufferType.DENOMINATOR -> numpadData.denominatorNumpadBuffer = NumpadBuffer()
                                }
                                numpadData.error = ""
                            },
                            icon = when (numpadData.currentBufferSelected) {
                                NumpadBufferType.NUMBER -> Res.drawable.icon_numpad_fraction_numerator
                                NumpadBufferType.NUMERATOR -> if (numpadData.numeratorNumpadBuffer?.buffer == "") Res.drawable.icon_numpad_fraction_clear else Res.drawable.icon_numpad_fraction_denominator
                                NumpadBufferType.DENOMINATOR -> Res.drawable.icon_numpad_fraction_clear
                            },
                            description = "Fraction"
                        )
                    }
                }
            }
        }
    }
}