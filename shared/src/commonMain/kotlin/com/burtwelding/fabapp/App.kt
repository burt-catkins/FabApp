package com.burtwelding.fabapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.Miter
import com.burtwelding.fabapp.ui.draw.MiterPaths
import org.kimplify.deci.Deci
import com.burtwelding.fabapp.storage.ItemHandle
import com.burtwelding.fabapp.storage.rememberStorage
import com.burtwelding.fabapp.ui.draw.getMiterPaths
import deci.trim
import kotlinx.serialization.Serializable

@Composable
@Preview
fun App() {
    var canvasSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    var leftPieceSizeString by remember { mutableStateOf("") }
    var rightPieceSizeString by remember { mutableStateOf("") }
    var totalJointAngleString by remember { mutableStateOf("") }

    var firstMiter by remember { mutableStateOf(true) }
    var showMiterDialog by remember { mutableStateOf(true) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var angleError by remember { mutableStateOf("") }
    var leftPieceSizeError by remember { mutableStateOf("") }
    var rightPieceSizeError by remember { mutableStateOf("") }
    var angleValidated by remember { mutableStateOf(false) }
    var leftPieceSizeValidated by remember { mutableStateOf(true) }
    var rightPieceSizeValidated by remember { mutableStateOf(true) }

    var miterCalculation by remember { mutableStateOf<Miter.MiterCalculation?>(null) }
    var miterPaths by remember { mutableStateOf<MiterPaths?>(null) }

    var leftMiterAngleString by remember { mutableStateOf("") }
    var leftMiterRiseString by remember { mutableStateOf("") }
    var leftMiterAngleSideString by remember { mutableStateOf("") }
    var rightMiterAngleString by remember { mutableStateOf("") }
    var rightMiterRiseString by remember { mutableStateOf("") }
    var rightMiterAngleSideString by remember { mutableStateOf("") }

    var inputFocus by remember { mutableStateOf<InputType?>(null) }

    var numberTypeSelected by rememberStorage("preferences/numberFormat", NumberTypeSelected.FRACTION)

    var heightOfTopSection by remember { mutableStateOf(0f) }

    val buttonColor by remember { mutableStateOf(ButtonColors(
        containerColor = Color(0.2f, 0.5f, 0.2f),
        contentColor = Color(0.1f, 0.1f, 0.1f),
        disabledContainerColor = Color(0f, 0.1f, 0f),
        disabledContentColor = Color(0.1f, 0f, 0f)
    )) }

    fun convertValues (calculation: Miter.MiterCalculation) {
        when (numberTypeSelected) {
            NumberTypeSelected.DECIMAL -> {
                leftMiterRiseString = calculation.leftMiterRise.baseUnitDecimalValue.trim(3)
                rightMiterRiseString = calculation.rightMiterRise.baseUnitDecimalValue.trim(3)
            }
            NumberTypeSelected.FRACTION -> {
                val leftRise = Calculator.getFractionalInches(
                    length = Length(Deci(calculation.leftMiterRise.baseUnitDecimalValue.trim(3))),
                    denominator = ImperialFraction.SIXTEENTHS
                )
                leftMiterRiseString = leftRise.toString()
                val rightRise = Calculator.getFractionalInches(
                    length = Length(Deci(calculation.rightMiterRise.baseUnitDecimalValue.trim(3))),
                    denominator = ImperialFraction.SIXTEENTHS
                )
                rightMiterRiseString = rightRise.toString()
            }
        }
    }

    @Composable
    fun Tile(
        modifier: Modifier = Modifier,
        text: String,
        pieceSide: Side?
    ) {
        Box(
            modifier = modifier
                .background(
                    color = when (pieceSide) {
                        Side.LEFT -> Color(red = 0.8f, green = 0.4f, blue = 0.4f)
                        Side.RIGHT -> Color(red = 0.4f, green = 0.4f, blue = 0.8f)
                        null -> Color(red = 0.6f, green = 0.3f, blue = 0.9f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        }
    }


    @Composable
    fun TileButton(
        modifier: Modifier = Modifier,
        text: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(4.dp),
            colors = buttonColor
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun BottomTileLayout(
        modifier: Modifier = Modifier
    ) {
        val spacing = 6.dp

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {

            // First two rows, with a tall tile on the left
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {

                // Tall tile (spans two rows)
                Tile(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = "Joint Angle:\n$totalJointAngleString°",
                    pieceSide = null
                )

                // Middle and right columns
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        Tile(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            text = "Size:\n$leftPieceSizeString",
                            pieceSide = Side.LEFT
                        )

                        Tile(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            text = "Size:\n$rightPieceSizeString",
                            pieceSide = Side.RIGHT
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        Tile(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            text = "Miter Angle:\n$leftMiterAngleString°",
                            pieceSide = Side.LEFT
                        )

                        Tile(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            text = "Miter Angle:\n$rightMiterAngleString°",
                            pieceSide = Side.RIGHT
                        )
                    }
                }
            }

            // Third row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                TileButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = "New",
                    onClick = { showMiterDialog = true }
                )
                Tile(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = "Miter Rise:\n$leftMiterRiseString",
                    pieceSide = Side.LEFT
                )
                Tile(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = "Miter Rise:\n$rightMiterRiseString",
                    pieceSide = Side.RIGHT
                )
            }

            // Fourth row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                TileButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = numberTypeSelected.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = {
                        numberTypeSelected = when (numberTypeSelected) {
                            NumberTypeSelected.DECIMAL -> NumberTypeSelected.FRACTION
                            NumberTypeSelected.FRACTION -> NumberTypeSelected.DECIMAL
                        }
                    }
                )
                Tile(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = "Angle Side:\n$leftMiterAngleSideString",
                    pieceSide = Side.LEFT
                )
                Tile(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    text = "Angle Side:\n$rightMiterAngleSideString",
                    pieceSide = Side.RIGHT
                )
            }
        }
    }

    @Composable
    fun MainScreen() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            // TOP SECTION - Scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .onGloballyPositioned {
                        heightOfTopSection = it.boundsInRoot().height
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .onSizeChanged{ newSize ->
                            canvasSize = newSize
                        }
                ) {
                    miterPaths?.let { paths ->
                        drawPath(
                            paths.leftMiterTrianglePath,
                            Color(red = 1f, green = 0.2f, blue = 0.2f)
                        )
                        drawPath(
                            paths.leftBoxPath,
                            Color(red = 1f, green = 0f, blue = 0f)
                        )
                        drawPath(
                            paths.rightMiterTrianglePath,
                            Color(blue = 1f, green = 0.2f, red = 0.2f)
                        )
                        drawPath(
                            paths.rightBoxPath,
                            Color(blue = 1f, green = 0f, red = 0f)
                        )
                    }
                }

            }


            // BOTTOM SECTION - Fixed height
            BottomTileLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }
    }

    fun createSetAndDrawMiter() {
        leftPieceSizeString = if (leftPieceSizeString == "") "1" else leftPieceSizeString
        rightPieceSizeString = if (rightPieceSizeString == "") "1" else rightPieceSizeString
        val leftPieceSizeItem = ItemHandle<Length>(
            Length(Deci(leftPieceSizeString))
        )
        val rightPieceSizeItem = ItemHandle<Length>(
            Length(Deci(rightPieceSizeString))
        )
        val totalJointAngleItem = ItemHandle<Angle>(
            Angle(Deci(totalJointAngleString))
        )
        val miter = Miter(
            leftPieceSize = leftPieceSizeItem,
            rightPieceSize = rightPieceSizeItem,
            totalJointAngle = totalJointAngleItem
        )
        val nonNullCalculation = miter.calculate()
        miterCalculation = nonNullCalculation
        miterPaths = getMiterPaths(
            leftPieceSizeItem = leftPieceSizeItem,
            rightPieceSizeItem = rightPieceSizeItem,
            totalJointAngleItem = totalJointAngleItem,
            miterCalculation = miterCalculation
                ?: throw IllegalArgumentException("u forgot to calculate!"),
            windowWidth = Deci(canvasSize.width),
            windowHeight = Deci(heightOfTopSection.toDouble())
        )
        leftMiterAngleString = nonNullCalculation.leftMiterAngle.baseUnitDecimalValue.trim(1)
        leftMiterRiseString = nonNullCalculation.leftMiterRise.baseUnitDecimalValue.trim(3)
        leftMiterAngleSideString = nonNullCalculation.leftMiterSide.name
        rightMiterAngleString = nonNullCalculation.rightMiterAngle.baseUnitDecimalValue.trim(1)
        rightMiterRiseString = nonNullCalculation.rightMiterRise.baseUnitDecimalValue.trim(3)
        rightMiterAngleSideString = nonNullCalculation.rightMiterSide.name
        convertValues(nonNullCalculation)
    }

    LaunchedEffect(numberTypeSelected) {
        miterCalculation?.let(::convertValues)
    }

    LaunchedEffect(canvasSize) {
        if (miterCalculation != null)
            createSetAndDrawMiter()
    }

    fun validateInput (inputType: InputType): Boolean {
        if (inputType == InputType.LEFT_PIECE_SIZE && leftPieceSizeString == "" && rightPieceSizeString == "") {
            leftPieceSizeValidated = true
            return true
        }
        if (inputType == InputType.RIGHT_PIECE_SIZE && rightPieceSizeString == "" && leftPieceSizeString == "") {
            rightPieceSizeValidated = true
            return true
        }
        try {
            val newValue = Deci(
                when (inputType) {
                    InputType.ANGLE -> totalJointAngleString
                    InputType.LEFT_PIECE_SIZE -> leftPieceSizeString
                    InputType.RIGHT_PIECE_SIZE -> rightPieceSizeString
                }
            )
            val newValueIsValid = when (inputType) {
                InputType.ANGLE -> newValue in Deci(0.01)..Deci(179.99)
                InputType.LEFT_PIECE_SIZE, InputType.RIGHT_PIECE_SIZE -> {
                    (leftPieceSizeString == "" && rightPieceSizeString == "")
                            || newValue > Deci.ZERO
                }
            }
            if (newValueIsValid) {
                when (inputType) {
                    InputType.ANGLE -> {
                        totalJointAngleString = newValue.toString()
                        angleValidated = true
                        angleError = ""
                    }
                    InputType.LEFT_PIECE_SIZE -> {
                        leftPieceSizeString = newValue.toString()
                        leftPieceSizeValidated = true
                        leftPieceSizeError = ""
                    }
                    InputType.RIGHT_PIECE_SIZE -> {
                        rightPieceSizeString = newValue.toString()
                        rightPieceSizeValidated = true
                        rightPieceSizeError = ""
                    }
                }
                keyboardController?.hide()
                return true
            } else {
                when (inputType) {
                    InputType.ANGLE -> {
                        angleValidated = false
                        angleError = "Enter an angle from 0.01 to 179.99"
                    }
                    InputType.LEFT_PIECE_SIZE -> {
                        leftPieceSizeValidated = false
                        leftPieceSizeError = "Value must be greater than 0"
                    }
                    InputType.RIGHT_PIECE_SIZE -> {
                        rightPieceSizeValidated = false
                        rightPieceSizeError = "Value must be greater than 0"
                    }
                }
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (inputType) {
                InputType.ANGLE -> {
                    angleValidated = false
                    angleError = "Please enter a valid decimal number"
                }
                InputType.LEFT_PIECE_SIZE -> {
                    leftPieceSizeValidated = false
                    leftPieceSizeError = "Please enter a valid decimal number"
                }
                InputType.RIGHT_PIECE_SIZE -> {
                    rightPieceSizeValidated = false
                    rightPieceSizeError = "Please enter a valid decimal number"
                }
            }
            return false
        }
    }

    fun attemptInput () {
        validateInput(InputType.ANGLE)
        validateInput(InputType.LEFT_PIECE_SIZE)
        validateInput(InputType.RIGHT_PIECE_SIZE)
        if (angleValidated && leftPieceSizeValidated && rightPieceSizeValidated) {
            if (firstMiter)
                firstMiter = !firstMiter
            createSetAndDrawMiter()
            keyboardController?.hide()
            showMiterDialog = false
        }
    }

    if (showMiterDialog) {
        AlertDialog(
            onDismissRequest = {
                keyboardController?.hide()
                if (!firstMiter)
                    showMiterDialog = false
            },
            title = {
                Text("Create Miter")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = totalJointAngleString,
                        onValueChange = { newValue ->
                            angleValidated = false
                            totalJointAngleString = newValue
                            angleError = ""
                        },
                        label = { Text("Total Joint Angle (Degrees)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                            showKeyboardOnFocus = true
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (validateInput(InputType.ANGLE))
                                    focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged{ focus ->
                                if (!focus.isFocused && inputFocus != null)
                                    validateInput(InputType.ANGLE)
                                else if (focus.isFocused)
                                    inputFocus = InputType.ANGLE
                            }
                    )
                    if (angleError.isNotEmpty()) {
                        Text(
                            text = angleError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    OutlinedTextField(
                        value = leftPieceSizeString,
                        onValueChange = { newValue ->
                            leftPieceSizeValidated = false
                            leftPieceSizeString = newValue
                            leftPieceSizeError = ""
                        },
                        label = { Text("Left Piece Size (Optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                            showKeyboardOnFocus = true
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (validateInput(InputType.LEFT_PIECE_SIZE))
                                    focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged{ focus ->
                                if (!focus.isFocused && inputFocus != null)
                                    validateInput(InputType.LEFT_PIECE_SIZE)
                                else if (focus.isFocused)
                                    inputFocus = InputType.LEFT_PIECE_SIZE
                            }
                    )
                    if (leftPieceSizeError.isNotEmpty()) {
                        Text(
                            text = leftPieceSizeError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    OutlinedTextField(
                        value = rightPieceSizeString,
                        onValueChange = { newValue ->
                            rightPieceSizeValidated = false
                            rightPieceSizeString = newValue
                            rightPieceSizeError = ""
                        },
                        label = { Text("Right Piece Size (Optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                            showKeyboardOnFocus = true
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (validateInput(InputType.RIGHT_PIECE_SIZE))
                                    attemptInput()
                            }
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged{ focus ->
                                if (!focus.isFocused && inputFocus != null)
                                    validateInput(InputType.RIGHT_PIECE_SIZE)
                                else if (focus.isFocused)
                                    inputFocus = InputType.RIGHT_PIECE_SIZE
                            }
                    )
                    if (rightPieceSizeError.isNotEmpty()) {
                        Text(
                            text = rightPieceSizeError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { attemptInput() }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                if (!firstMiter) {
                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            showMiterDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
    // If not null
    miterCalculation?.let {
        MainScreen()
    }

}




//        Column(
//            modifier = Modifier.fillMaxSize().padding(8.dp).zIndex(4f)
//        ) {
//            Spacer(modifier = Modifier.weight(1f))
//            MiterInfo(
//                jointAngle = totalJointAngleString,
//                leftPieceSize = leftPieceSizeString,
//                leftPieceMiterAngle = leftMiterAngleString,
//                leftPieceRise = leftMiterRiseString,
//                rightPieceSize = rightPieceSizeString,
//                rightPieceMiterAngle = rightMiterAngleString,
//                rightPieceRise = rightMiterRiseString,
//                leftPieceMiterSide = leftMiterAngleSideString,
//                rightPieceMiterSide = rightMiterAngleSideString
//            )
//        }


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



//    @Composable
//    fun MiterInfo(
//        jointAngle: String,
//        leftPieceSize: String,
//        leftPieceMiterAngle: String,
//        leftPieceRise: String,
//        rightPieceSize: String,
//        rightPieceMiterAngle: String,
//        rightPieceRise: String,
//        leftPieceMiterSide: String,
//        rightPieceMiterSide: String
//    ) {
//        @Composable
//        fun VariableTile(
//            label: String,
//            value: String,
//            tileColor: Color,
//            modifier: Modifier = Modifier
//        ) {
//            Box(
//                modifier = modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
//                    .background(tileColor)
//                    .padding(8.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = label,
//                        fontSize = 10.sp,
//                        color = Color(0.9f, 0.9f, 0.9f)
//                    )
//
//                    Text(
//                        text = value,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
//                }
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .zIndex(2f),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Button(
//                onClick = {
//                    numberTypeSelected = when (numberTypeSelected) {
//                        NumberTypeSelected.FRACTION -> NumberTypeSelected.DECIMAL
//                        NumberTypeSelected.DECIMAL -> NumberTypeSelected.FRACTION
//                    }
//                    convertValues(miterCalculation?: throw NullPointerException("bruh where da calc at"))
//                },
//                shape = ButtonDefaults.shape,
//            ) { Text(numberTypeSelected.name.lowercase().replaceFirstChar{it.uppercase()}) }
//            // Left column - one large tile
//            Column(
//                modifier = Modifier.weight(2f),
//            ) {
//                VariableTile(
//                    label = "Joint Angle",
//                    value = "$jointAngle°",
//                    tileColor = Color(red = 0.3f, green = 0.1f, blue = 0.5f),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                )
//            }
//
//            Row (
//
//            ) {
//                val redTileColor = Color(red = 0.5f, green = 0.2f, blue = 0.2f)
//                // Middle column - three tiles
//                Column(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    VariableTile("Size", leftPieceSize, redTileColor)
//                    VariableTile("Miter Angle", "${leftPieceMiterAngle}°", redTileColor)
//                    VariableTile("Rise", leftPieceRise, redTileColor)
//                    VariableTile("Angle Side", leftPieceMiterSide, redTileColor)
//                }
//
//                val blueTileColor = Color(red = 0.2f, green = 0.2f, blue = 0.5f)
//                // Right column - three tiles
//                Column(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    VariableTile("Size", rightPieceSize, blueTileColor)
//                    VariableTile("Miter Angle", "${rightPieceMiterAngle}°", blueTileColor)
//                    VariableTile("Rise", rightPieceRise, blueTileColor)
//                    VariableTile("Angle Side", rightPieceMiterSide, blueTileColor)
//                }
//                Button(
//                    onClick = {
//                        showMiterDialog = true
//                    },
//                    shape = ButtonDefaults.shape,
//                ) { Text("New") }
//            }
//        }
//    }


enum class Side {
    LEFT,
    RIGHT
}

enum class InputType {
    ANGLE,
    LEFT_PIECE_SIZE,
    RIGHT_PIECE_SIZE
}

@Serializable
enum class NumberTypeSelected {
    DECIMAL,
    FRACTION
}