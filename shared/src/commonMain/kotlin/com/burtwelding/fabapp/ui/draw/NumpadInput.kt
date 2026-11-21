package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Number
import org.kimplify.deci.Deci

@Composable
fun NumpadInput (
    appState: AppState
) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    @Composable
    fun Tile (
        modifier: Modifier,
        function: () -> Unit,
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
                    .clickable { function() }
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
        if (error != "") {
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
                    text = error,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            }
        }
        // Text Panel
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
            Text(
                text = input,
                modifier = Modifier.align(Alignment.Center),
                fontSize = TextUnit(25f, TextUnitType.Sp)
            )
        }
        // Numpad, top 2
        Row (modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        ) {
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                function = { input = "" },
                text = "CLEAR"
            )
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "7" },
                    text = "7"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "4" },
                    text = "4"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "8" },
                    text = "8"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "5" },
                    text = "5"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "9" },
                    text = "9"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "6" },
                    text = "6"
                )
            }
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                function = {
                    try {
                        if (input != "") Deci(input)
                        val validationTextResult = appState.numpadValidateFunction(input)
                        if (validationTextResult == "") {
                            appState.numpadOutputFunction(input)
                            appState.openNumpad = false
                        } else {
                            error = validationTextResult
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        error = "Please enter a valid number"
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
                function = { input = input.dropLast(1) },
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
                        function = { input += "1" },
                        text = "1"
                    )
                    Tile(
                        modifier = Modifier.weight(1f),
                        function = { input += "2" },
                        text = "2"
                    )
                }
                Tile(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    function = { input += "0" },
                    text = "0"
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight().weight(1f)
            ) {

                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "3" },
                    text = "3"
                )
                Tile(
                    modifier = Modifier.weight(1f),
                    function = { input += "." },
                    text = "."
                )
            }
            Tile(
                modifier = Modifier.fillMaxHeight().weight(1f),
                function = { },
                text = "Fraction"
            )
        }
    }
}