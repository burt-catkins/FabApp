package com.burtwelding.fabapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.storage.ItemHandle


import fabapp.shared.generated.resources.Res
import fabapp.shared.generated.resources.compose_multiplatform
import org.kimplify.deci.Deci
import org.kimplify.deci.extension.toFloat

@Composable
@Preview
fun App() {

    Canvas(

        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)

    ) {

        val maxPieceSize = size.width * 0.35f

        val leftPieceSize = ItemHandle<Length>(Length(Deci(1)))
        val rightPieceSize = ItemHandle<Length>(Length(Deci(1)))
        val totalJointAngle = ItemHandle<Angle>(Angle(Deci(135)))

        val miter = Miter(
            leftPieceSize = leftPieceSize,
            rightPieceSize = rightPieceSize,
            totalJointAngle = totalJointAngle
        )

        val miterCalculation = miter.calculate()
        val leftMiterRise = miterCalculation.leftMiterRise.baseUnitDecimalValue
        val rightMiterRise = miterCalculation.rightMiterRise
        val leftMiterAngleIsOutside = miterCalculation.leftMiterSide == Miter.MiterAngleSide.OUTSIDE
        val leftMiterAngle = miterCalculation.leftMiterAngle
        val rightMiterAngle = miterCalculation.rightMiterAngle
        val rightMiterAngleIsOutside = miterCalculation.rightMiterSide == Miter.MiterAngleSide.OUTSIDE
        val leftPieceBoxSize =
            if (leftPieceSize.item.baseUnitDecimalValue > rightPieceSize.item.baseUnitDecimalValue)
                maxPieceSize
            else
                maxPieceSize * leftPieceSize.item.baseUnitDecimalValue.toFloat() / rightPieceSize.item.baseUnitDecimalValue.toFloat()
        val rightPieceBoxSize =
            if (rightPieceSize.item.baseUnitDecimalValue > leftPieceSize.item.baseUnitDecimalValue)
                maxPieceSize
            else
                maxPieceSize * rightPieceSize.item.baseUnitDecimalValue.toFloat() / leftPieceSize.item.baseUnitDecimalValue.toFloat()
        val leftMiterRiseUI = leftMiterRise.toFloat() / leftPieceSize.item.baseUnitDecimalValue.toFloat() * leftPieceBoxSize
        val leftPieceVerticalOffset = if (leftMiterAngleIsOutside) leftMiterRiseUI else 0f
        val leftPieceBox = Path().apply {
            moveTo(size.width/2, size.height/2 + leftPieceVerticalOffset)
            lineTo(size.width/2, size.height)
            lineTo(size.width/2 - leftPieceBoxSize, size.height)
            lineTo(size.width/2 - leftPieceBoxSize, size.height/2 + leftPieceVerticalOffset)
            close()
        }
        val leftMiterRightTrianglePath = Path().apply {
            moveTo(size.width/2, size.height/2 + leftPieceVerticalOffset)
            lineTo(size.width/2 - leftPieceBoxSize, size.height/2 + leftPieceVerticalOffset)
            lineTo(size.width/2 - if (leftMiterAngleIsOutside) 0f else leftPieceBoxSize, size.height/2 + leftPieceVerticalOffset - leftMiterRiseUI)
            close()
        }

        drawPath(
            path = leftPieceBox,
            color = Color(
                red = 1f,
                green = 0f,
                blue = 0f
            )
        )
        drawPath(
            path = leftMiterRightTrianglePath,
            color = Color(
                red = 1f,
                green = 0.2f,
                blue = 0.2f
            )
        )

    }

//    @Composable
//    fun Circle(angle: Float) {
//        Canvas(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            val radius = size.minDimension * 0.4f
//
//            // Red pie slice
//            drawArc(
//                color = Color.Red,
//                startAngle = -90f,
//                sweepAngle = angle,
//                useCenter = true,
//                topLeft = Offset(
//                    x = center.x - radius,
//                    y = center.y - radius
//                ),
//                size = androidx.compose.ui.geometry.Size(
//                    width = radius * 2,
//                    height = radius * 2
//                )
//            )
//
//            // Circle outline
//            drawCircle(
//                color = Color.Black,
//                radius = radius,
//                center = center,
//                style = Stroke(width = 3.dp.toPx())
//            )
//        }
//    }
//
//    var angle by remember { mutableFloatStateOf(30f) }
//    var inputAngle by remember { mutableStateOf("30") }
//    var showDialog by remember { mutableStateOf(false) }
//    var error by remember { mutableStateOf("") }
//
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val focusRequester = remember { FocusRequester() }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//
//        // --------------------------------
//        // TOP TITLE BAR
//        // --------------------------------
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp)
//                .background(Color(0xFF26364A))
//                .padding(horizontal = 16.dp),
//            contentAlignment = Alignment.CenterStart
//        ) {
//            Text(
//                text = "My Circle App",
//                color = Color.White,
//                style = MaterialTheme.typography.titleLarge
//            )
//        }
//
//        // --------------------------------
//        // MAIN CONTENT AREA
//        // --------------------------------
//        Row(
//            modifier = Modifier.weight(1f)
//        ) {
//
//            // --------------------------------
//            // LEFT PANE
//            // --------------------------------
//            Column(
//                modifier = Modifier
//                    .width(72.dp)
//                    .fillMaxHeight()
//                    .background(Color(0xFF34495E))
//                    .padding(vertical = 12.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                IconButton(onClick = { /* Open Circle program */ }) {
//                    Icon(
//                        imageVector = Icons,
//                        contentDescription = "Circle",
//                        tint = Color.White
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                IconButton(onClick = { /* Open Program 2 */ }) {
//                    Icon(
//                        imageVector = Icons.Default.Settings,
//                        contentDescription = "Settings",
//                        tint = Color.White
//                    )
//                }
//            }
//
//            // --------------------------------
//            // RIGHT CONTENT AREA
//            // --------------------------------
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight()
//            ) {
//
//                // CIRCLE CANVAS AREA (70%)
//                Box(
//                    modifier = Modifier
//                        .weight(0.7f)
//                        .fillMaxWidth()
//                        .background(Color(0xFFF1F3F5)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Circle(angle = angle)
//                }
//
//                // BOTTOM PANEL (30%)
//                Column(
//                    modifier = Modifier
//                        .weight(0.3f)
//                        .fillMaxWidth()
//                        .background(Color(0xFFD6DEE8))
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Circle Controls",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text("Current angle: $angle°")
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Button(
//                        onClick = {
//                            inputAngle = angle.toString()
//                            error = ""
//                            showDialog = true
//                        }
//                    ) {
//                        Text("Set Angle")
//                    }
//                }
//            }
//        }
//    }
//
//    // --------------------------------
//    // ANGLE INPUT DIALOG
//    // --------------------------------
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = {
//                keyboardController?.hide()
//                showDialog = false
//            },
//            title = {
//                Text("Enter Angle")
//            },
//            text = {
//                Column {
//                    OutlinedTextField(
//                        value = inputAngle,
//                        onValueChange = {
//                            inputAngle = it
//                            error = ""
//                        },
//                        label = { Text("Degrees") },
//                        suffix = { Text("°") },
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Decimal,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = {
//                                val newAngle = inputAngle.toFloatOrNull()
//
//                                if (newAngle != null && newAngle in 0f..360f) {
//                                    angle = newAngle
//                                    keyboardController?.hide()
//                                    showDialog = false
//                                } else {
//                                    error = "Enter an angle from 0 to 360"
//                                }
//                            }
//                        ),
//                        modifier = Modifier.focusRequester(focusRequester)
//                    )
//
//                    if (error.isNotEmpty()) {
//                        Text(
//                            text = error,
//                            color = MaterialTheme.colorScheme.error
//                        )
//                    }
//                }
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        val newAngle = inputAngle.toFloatOrNull()
//
//                        if (newAngle != null && newAngle in 0f..360f) {
//                            angle = newAngle
//                            keyboardController?.hide()
//                            showDialog = false
//                        } else {
//                            error = "Enter an angle from 0 to 360"
//                        }
//                    }
//                ) {
//                    Text("Apply")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        keyboardController?.hide()
//                        showDialog = false
//                    }
//                ) {
//                    Text("Cancel")
//                }
//            }
//        )
//
//        LaunchedEffect(Unit) {
//            focusRequester.requestFocus()
//        }
//    }
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//            Circle(angle = 30f)
//        }
//    }
}