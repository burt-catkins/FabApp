//package com.burtwelding.fabapp.ui.draw.miter
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import com.burtwelding.fabapp.InputType
//
//@Composable
//fun CreateMiterDialog () {
//    AlertDialog(
//        onDismissRequest = {
//            keyboardController?.hide()
//            if (!firstMiter)
//                showMiterDialog = false
//        },
//        title = {
//            Text("Create Miter")
//        },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = totalJointAngleString,
//                    onValueChange = { newValue ->
//                        angleValidated = false
//                        totalJointAngleString = newValue
//                        angleError = ""
//                    },
//                    label = { Text("Total Joint Angle (Degrees)") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Decimal,
//                        imeAction = ImeAction.Next,
//                        showKeyboardOnFocus = true
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onNext = {
//                            if (validateInput(InputType.ANGLE)) {
//                                defaultKeyboardAction(ImeAction.Next)
//                            }
//                        }
//                    ),
//                    modifier = Modifier
//                        .focusRequester(focusRequester)
//                        .onFocusChanged{ focus ->
//                            if (!focus.isFocused && inputFocus != null)
//                                validateInput(InputType.ANGLE)
//                            else if (focus.isFocused)
//                                inputFocus = InputType.ANGLE
//                        }
//                )
//                if (angleError.isNotEmpty()) {
//                    Text(
//                        text = angleError,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//                OutlinedTextField(
//                    value = leftPieceSizeString,
//                    onValueChange = { newValue ->
//                        leftPieceSizeValidated = false
//                        leftPieceSizeString = newValue
//                        leftPieceSizeError = ""
//                    },
//                    label = { Text("Left Piece Size (Optional)") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Decimal,
//                        imeAction = ImeAction.Next,
//                        showKeyboardOnFocus = true
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onNext = {
//                            if (validateInput(InputType.LEFT_PIECE_SIZE)) {
//                                defaultKeyboardAction(ImeAction.Next)
//                            }
//                        }
//                    ),
//                    modifier = Modifier
//                        .onFocusChanged{ focus ->
//                            if (!focus.isFocused && inputFocus != null)
//                                validateInput(InputType.LEFT_PIECE_SIZE)
//                            else if (focus.isFocused)
//                                inputFocus = InputType.LEFT_PIECE_SIZE
//                        }
//                )
//                if (leftPieceSizeError.isNotEmpty()) {
//                    Text(
//                        text = leftPieceSizeError,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//                OutlinedTextField(
//                    value = rightPieceSizeString,
//                    onValueChange = { newValue ->
//                        rightPieceSizeValidated = false
//                        rightPieceSizeString = newValue
//                        rightPieceSizeError = ""
//                    },
//                    label = { Text("Right Piece Size (Optional)") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Decimal,
//                        imeAction = ImeAction.Done,
//                        showKeyboardOnFocus = true
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            if (validateInput(InputType.RIGHT_PIECE_SIZE))
//                                attemptInput()
//                        }
//                    ),
//                    modifier = Modifier
//                        .onFocusChanged{ focus ->
//                            if (!focus.isFocused && inputFocus != null)
//                                validateInput(InputType.RIGHT_PIECE_SIZE)
//                            else if (focus.isFocused)
//                                inputFocus = InputType.RIGHT_PIECE_SIZE
//                        }
//                )
//                if (rightPieceSizeError.isNotEmpty()) {
//                    Text(
//                        text = rightPieceSizeError,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//
//            LaunchedEffect(Unit) {
//                focusRequester.requestFocus()
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = { attemptInput() }
//            ) {
//                Text("Apply")
//            }
//        },
//        dismissButton = {
//            if (!firstMiter) {
//                TextButton(
//                    onClick = {
//                        keyboardController?.hide()
//                        showMiterDialog = false
//                    }
//                ) {
//                    Text("Cancel")
//                }
//            }
//        }
//    )
//}