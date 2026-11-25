package com.burtwelding.fabapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.programs.right_triangle.RightTriangleProgram
import com.burtwelding.fabapp.ui.draw.NumpadInput
import com.burtwelding.fabapp.ui.draw.ProgramPane

@Composable
@Preview
fun App() {
    // TODO: When anything inside this is changed, everything is redrawn! Mainly an issue for the Program Pane
    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val screenHeightPx = constraints.maxHeight
//        val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        if (screenHeightPx > 0f) {
            val globalDensity = screenHeightPx / 1280f
//            val globalDensityBasedOnWidth = screenWidthPx / 720f
//            val globalDensity = minOf(globalDensityBasedOnWidth, globalDensityBasedOnHeight)
            CompositionLocalProvider(
                value = LocalDensity provides Density(
                    density = globalDensity,
                    fontScale = 1f
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ProgramPane(
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .fillMaxHeight()
                                .weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(6f)
                                .background(Color(0.1f, 0.1f, 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clipToBounds() // clipToBounds makes it so that nothing can be drawn outside this box
                                    .padding(vertical = 10.dp)
                            ) {
                                when (AppState.currentProgram) {
                                    Programs.RIGHT_TRIANGLE -> RightTriangleProgram()
                                    Programs.MITER -> MiterProgram()
                                }
                            }
                            if (AppState.openNumpad) {
                                NumpadInput()
                            }
                        }
                    }
                }
            }
        }

    }

}

//
//    Draw(currentProgram)

//    when (currentProgram) {
//        Programs.MITER -> miter()
//        Programs.RIGHT_TRIANGLE -> rightTriangle()
//    }

//    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
//
//    var leftPieceSizeString by remember { mutableStateOf("") }
//    var rightPieceSizeString by remember { mutableStateOf("") }
//    var totalJointAngleString by remember { mutableStateOf("") }
//
//    var firstMiter by remember { mutableStateOf(true) }
//    var showMiterDialog by remember { mutableStateOf(true) }
//
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val focusRequester = remember { FocusRequester() }
//    var angleError by remember { mutableStateOf("") }
//    var leftPieceSizeError by remember { mutableStateOf("") }
//    var rightPieceSizeError by remember { mutableStateOf("") }
//    var angleValidated by remember { mutableStateOf(false) }
//    var leftPieceSizeValidated by remember { mutableStateOf(true) }
//    var rightPieceSizeValidated by remember { mutableStateOf(true) }
//
//    var miterCalculation by remember { mutableStateOf<Miter.MiterCalculation?>(null) }
//    var miterPaths by remember { mutableStateOf<MiterPaths?>(null) }
//
//    var leftMiterAngleString by remember { mutableStateOf("") }
//    var leftMiterRiseString by remember { mutableStateOf("") }
//    var leftMiterAngleSideString by remember { mutableStateOf("") }
//    var rightMiterAngleString by remember { mutableStateOf("") }
//    var rightMiterRiseString by remember { mutableStateOf("") }
//    var rightMiterAngleSideString by remember { mutableStateOf("") }
//
//    var inputFocus by remember { mutableStateOf<InputType?>(null) }

//     var numberTypeSelected by rememberStorage("preferences/numberFormat", NumberTypeSelected.FRACTION)
////    var numberTypeSelected by remember { mutableStateOf(NumberTypeSelected.FRACTION) }
//
//    var heightOfTopSection by remember { mutableStateOf(0f) }

//    val buttonColor by remember { mutableStateOf(ButtonColors(
//        containerColor = Color(0.2f, 0.5f, 0.2f),
//        contentColor = Color(0.1f, 0.1f, 0.1f),
////        disabledContainerColor = Color(0f, 0.1f, 0f),
////        disabledContentColor = Color(0.1f, 0f, 0f)
////    )) }
//
//    fun convertValues (calculation: Miter.MiterCalculation) {
//        when (numberTypeSelected) {
//            NumberTypeSelected.DECIMAL -> {
//                leftMiterRiseString = calculation.leftMiterRise.baseUnitDecimalValue.trim(3)
//                rightMiterRiseString = calculation.rightMiterRise.baseUnitDecimalValue.trim(3)
//            }
//            NumberTypeSelected.FRACTION -> {
//                val leftRise = Calculator.getFractionalInches(
//                    length = Length(Deci(calculation.leftMiterRise.baseUnitDecimalValue.trim(3))),
//                    denominator = ImperialFraction.SIXTEENTHS
//                )
//                leftMiterRiseString = leftRise.toString()
//                val rightRise = Calculator.getFractionalInches(
//                    length = Length(Deci(calculation.rightMiterRise.baseUnitDecimalValue.trim(3))),
//                    denominator = ImperialFraction.SIXTEENTHS
//                )
//                rightMiterRiseString = rightRise.toString()
//            }
//        }
//    }
//
//    fun createSetAndDrawMiter() {
//        leftPieceSizeString = if (leftPieceSizeString == "") "1" else leftPieceSizeString
//        rightPieceSizeString = if (rightPieceSizeString == "") "1" else rightPieceSizeString
//        val leftPieceSizeItem = ItemHandle<Length>(
//            Length(Deci(leftPieceSizeString))
//        )
//        val rightPieceSizeItem = ItemHandle<Length>(
//            Length(Deci(rightPieceSizeString))
//        )
//        val totalJointAngleItem = ItemHandle<Angle>(
//            Angle(Deci(totalJointAngleString))
//        )
//        val miter = Miter(
//            leftPieceSize = leftPieceSizeItem,
//            rightPieceSize = rightPieceSizeItem,
//            totalJointAngle = totalJointAngleItem
//        )
//        val nonNullCalculation = miter.calculate()
//        miterCalculation = nonNullCalculation
//        miterPaths = getMiterPaths(
//            leftPieceSizeItem = leftPieceSizeItem,
//            rightPieceSizeItem = rightPieceSizeItem,
//            totalJointAngleItem = totalJointAngleItem,
//            miterCalculation = miterCalculation
//                ?: throw IllegalArgumentException("u forgot to calculate!"),
//            windowWidth = Deci(canvasSize.width),
//            windowHeight = Deci(heightOfTopSection.toDouble())
//        )
//        leftMiterAngleString = nonNullCalculation.leftMiterAngle.baseUnitDecimalValue.trim(1)
//        leftMiterRiseString = nonNullCalculation.leftMiterRise.baseUnitDecimalValue.trim(3)
//        leftMiterAngleSideString = nonNullCalculation.leftMiterSide.name
//        rightMiterAngleString = nonNullCalculation.rightMiterAngle.baseUnitDecimalValue.trim(1)
//        rightMiterRiseString = nonNullCalculation.rightMiterRise.baseUnitDecimalValue.trim(3)
//        rightMiterAngleSideString = nonNullCalculation.rightMiterSide.name
//        convertValues(nonNullCalculation)
//    }
//
//    LaunchedEffect(numberTypeSelected) {
//        miterCalculation?.let(::convertValues)
//    }
//
//    LaunchedEffect(canvasSize) {
//        if (miterCalculation != null)
//            createSetAndDrawMiter()
//    }
//
//    fun validateInput (inputType: InputType): Boolean {
//        if (inputType == InputType.LEFT_PIECE_SIZE && leftPieceSizeString == "" && rightPieceSizeString == "") {
//            leftPieceSizeValidated = true
//            return true
//        }
//        if (inputType == InputType.RIGHT_PIECE_SIZE && rightPieceSizeString == "" && leftPieceSizeString == "") {
//            rightPieceSizeValidated = true
//            return true
//        }
//        try {
//            val newValue = Deci(
//                when (inputType) {
//                    InputType.ANGLE -> totalJointAngleString
//                    InputType.LEFT_PIECE_SIZE -> leftPieceSizeString
//                    InputType.RIGHT_PIECE_SIZE -> rightPieceSizeString
//                }
//            )
//            val newValueIsValid = when (inputType) {
//                InputType.ANGLE -> newValue in Deci(0.01)..Deci(179.99)
//                InputType.LEFT_PIECE_SIZE, InputType.RIGHT_PIECE_SIZE -> {
//                    (leftPieceSizeString == "" && rightPieceSizeString == "")
//                            || newValue > Deci.ZERO
//                }
//            }
//            if (newValueIsValid) {
//                when (inputType) {
//                    InputType.ANGLE -> {
//                        totalJointAngleString = newValue.toString()
//                        angleValidated = true
//                        angleError = ""
//                    }
//                    InputType.LEFT_PIECE_SIZE -> {
//                        leftPieceSizeString = newValue.toString()
//                        leftPieceSizeValidated = true
//                        leftPieceSizeError = ""
//                    }
//                    InputType.RIGHT_PIECE_SIZE -> {
//                        rightPieceSizeString = newValue.toString()
//                        rightPieceSizeValidated = true
//                        rightPieceSizeError = ""
//                    }
//                }
//                return true
//            } else {
//                when (inputType) {
//                    InputType.ANGLE -> {
//                        angleValidated = false
//                        angleError = "Enter an angle from 0.01 to 179.99"
//                    }
//                    InputType.LEFT_PIECE_SIZE -> {
//                        leftPieceSizeValidated = false
//                        leftPieceSizeError = "Value must be greater than 0"
//                    }
//                    InputType.RIGHT_PIECE_SIZE -> {
//                        rightPieceSizeValidated = false
//                        rightPieceSizeError = "Value must be greater than 0"
//                    }
//                }
//                return false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            when (inputType) {
//                InputType.ANGLE -> {
//                    angleValidated = false
//                    angleError = "Please enter a valid decimal number"
//                }
//                InputType.LEFT_PIECE_SIZE -> {
//                    leftPieceSizeValidated = false
//                    leftPieceSizeError = "Please enter a valid decimal number"
//                }
//                InputType.RIGHT_PIECE_SIZE -> {
//                    rightPieceSizeValidated = false
//                    rightPieceSizeError = "Please enter a valid decimal number"
//                }
//            }
//            return false
//        }
//    }
//
//    fun attemptInput () {
//        validateInput(InputType.ANGLE)
//        validateInput(InputType.LEFT_PIECE_SIZE)
//        validateInput(InputType.RIGHT_PIECE_SIZE)
//        if (angleValidated && leftPieceSizeValidated && rightPieceSizeValidated) {
//            if (firstMiter)
//                firstMiter = !firstMiter
//            createSetAndDrawMiter()
//            keyboardController?.hide()
//            showMiterDialog = false
//        }
//    }
//
//    if (showMiterDialog) {
//        CreateMiterDialog()
//    }
//    // If not null
//    miterCalculation?.let {
//        MainScreen()
//    }
//
//}




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


