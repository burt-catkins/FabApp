package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.ImperialFraction
import com.burtwelding.fabapp.darken
import org.kimplify.deci.Deci
import com.burtwelding.fabapp.Number
import com.burtwelding.fabapp.gray
import com.burtwelding.fabapp.programs.right_triangle.RightTriangleData.maxDenominator
import com.burtwelding.fabapp.programs.right_triangle.RightTriangleData.numbers
import com.burtwelding.fabapp.programs.right_triangle.RightTriangleData.operands
import com.burtwelding.fabapp.ui.draw.NumberText
import fabapp.shared.generated.resources.Res
import fabapp.shared.generated.resources.icon_reset
import org.jetbrains.compose.resources.painterResource

@Composable
fun RightTriangleInfoPanel () {
    @Composable
    fun Tile (
        modifier: Modifier,
        color: Color,
        subTile: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .background(color)
        ) {
            subTile()
        }
    }
    @Composable
    fun ActionButton (
        modifier: Modifier,
        color: Color,
        action: () -> Unit,
        centeredComposable: @Composable () -> Unit
    ) {
        Tile(
            modifier.clickable { action() },
            color,
            subTile = {
                Box (
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    centeredComposable()
                }
            }
        )
    }
    @Composable
    fun InfoTileButton (
        modifier: Modifier,
        operandType: RightTriangleOperandType,
        validateFunction: (Number?) -> String,
        validInputFunction: (Number?) -> Unit
    ) {
        Tile(
            color = operandType.color.gray(0.55f).darken(0.6f),
            modifier = modifier,
            subTile = {
                Box(
                    modifier = modifier
                        .clickable (enabled = operands.size < 2) {
                            RightTriangleData.numpadData.numpadOutputFunction = { validOutput ->
                                validInputFunction(validOutput)
                                if (operands.size == 2) {
                                    val operandKeys = operands.keys
                                    val firstOperandType = operandKeys.first()
                                    val secondOperandType = operandKeys.last()
                                    fun getOperand (operandType: RightTriangleOperandType) =
                                        when (operandType) {
                                            RightTriangleOperandType.RISE -> RightTriangleCalculator.Rise(
                                                operands[operandType]?.toDeci()
                                                    ?: error("bruh the operand type is rise but there aint no number")
                                            )
                                            RightTriangleOperandType.RUN -> RightTriangleCalculator.Run(
                                                operands[operandType]?.toDeci()
                                                    ?: error("bruh the operand type is run but there aint no number")
                                            )
                                            RightTriangleOperandType.DIAGONAL -> RightTriangleCalculator.Diagonal(
                                                operands[operandType]?.toDeci()
                                                    ?: error("bruh the operand type is diagonal but there aint no number")
                                            )
                                            RightTriangleOperandType.TOP_ANGLE -> RightTriangleCalculator.TopAngle(
                                                operands[operandType]?.toDeci()
                                                    ?: error("bruh the operand type is top angle but there aint no number")
                                            )
                                            RightTriangleOperandType.BOTTOM_ANGLE -> RightTriangleCalculator.BottomAngle(
                                                operands[operandType]?.toDeci()
                                                    ?: error("bruh the operand type is bottom angle but there aint no number")
                                            )
                                        }
                                    val calculation = RightTriangleCalculator(
                                        getOperand(firstOperandType),
                                        getOperand(secondOperandType)
                                    ).calculate()
                                    RightTriangleData.rightTriangleCalculation = calculation
                                    numbers[RightTriangleOperandType.RISE] = Number(calculation[RightTriangleOperandType.RISE]?: error("yoooooooo the rise calculation is null!!"))
                                    numbers[RightTriangleOperandType.RUN] = Number(calculation[RightTriangleOperandType.RUN]?: error("yoooooooo the RUN calculation is null!!"))
                                    numbers[RightTriangleOperandType.DIAGONAL] = Number(calculation[RightTriangleOperandType.DIAGONAL]?: error("yoooooooo the DIAGONAL calculation is null!!"))
                                    numbers[RightTriangleOperandType.TOP_ANGLE] = Number(calculation[RightTriangleOperandType.TOP_ANGLE]?: error("yoooooooo the TOP_ANGLE calculation is null!!"))
                                    numbers[RightTriangleOperandType.BOTTOM_ANGLE] = Number(calculation[RightTriangleOperandType.BOTTOM_ANGLE]?: error("yoooooooo the BOTTOM_ANGLE calculation is null!!"))
                                }
                            }
                            RightTriangleData.numpadData.numpadValidateFunction = validateFunction
                            RightTriangleData.numpadData.enabled = true
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        BoxWithConstraints (
                            modifier = Modifier
                                .fillMaxHeight(0.25f)
                                .fillMaxWidth()
                                .padding(2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = operandType.name.replace('_', ' '),
                                maxLines = 1,
                                fontSize = maxHeight.value.sp * 0.8f,
                                color = Color.White
                            )
                        }
                        Canvas (
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            drawLine(
                                color = Color.White,
                                strokeWidth = 2.dp.toPx(),
                                start = Offset(
                                    x = size.width * 1 / 10,
                                    y = 0f
                                ),
                                end = Offset(
                                    x = size.width * 9 / 10,
                                    y = 0f
                                )
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.8f)
                                    .fillMaxWidth(0.9f)
                            ) {
                                NumberText(
                                    number = numbers[operandType],
                                    fontColor = Color.White,
                                    maxDenominator = when (operandType) {
                                        RightTriangleOperandType.RISE, RightTriangleOperandType.RUN, RightTriangleOperandType.DIAGONAL -> maxDenominator.denominator
                                        RightTriangleOperandType.TOP_ANGLE, RightTriangleOperandType.BOTTOM_ANGLE -> null
                                    },
                                    decimalPrecision = 1
                                )
                            }
                        }
                    }
                }
            }
        )
    }
    val spacing = 10.dp
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(
                modifier = Modifier
                    .weight(1f),
                color = Color.Gray.darken(0.5f),
                action = { RightTriangleData.reset() },
                centeredComposable = {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        painter = painterResource(Res.drawable.icon_reset),
                        contentDescription = "Reset"
                    )
                }
            )
            ActionButton(
                modifier = Modifier
                    .weight(2f),
                color = Color.Gray.darken(0.2f),
                action = { maxDenominator = when (maxDenominator) {
                    ImperialFraction.WHOLE -> ImperialFraction.SIXTY_FOURTHS
                    ImperialFraction.HALVES -> ImperialFraction.WHOLE
                    ImperialFraction.QUARTERS -> ImperialFraction.HALVES
                    ImperialFraction.EIGHTHS -> ImperialFraction.QUARTERS
                    ImperialFraction.SIXTEENTHS -> ImperialFraction.EIGHTHS
                    ImperialFraction.THIRTY_SECONDS -> ImperialFraction.SIXTEENTHS
                    ImperialFraction.SIXTY_FOURTHS -> ImperialFraction.THIRTY_SECONDS
                } },
                centeredComposable = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.3f)
                    ) {
                        if (maxDenominator == ImperialFraction.WHOLE) {
                            NumberText(
                                decimal = null,
                                numerator = "1",
                                denominator = "1",
                                fontColor = Color.Black,
                                roundResult = null
                            )
                        } else {
                            NumberText(
                                Number(null, Fraction(Deci.ONE, maxDenominator.denominator)),
                                fontColor = Color.Black,
                                maxDenominator = maxDenominator.denominator
                            )
                        }
                    }
                }
            )
        }
        Column (
            modifier = Modifier
                .fillMaxHeight().weight(5f),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = RightTriangleOperandType.RISE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Rise must be greater than 0"
                    else if (
                        run {
                            val diagonal = operands[RightTriangleOperandType.DIAGONAL]
                            diagonal?.let { diagonal <= input }?: false
                        })
                        "Rise must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(RightTriangleOperandType.RISE)
                        numbers.remove(RightTriangleOperandType.RISE)
                    } else {
                        operands[RightTriangleOperandType.RISE] = validInput
                        numbers[RightTriangleOperandType.RISE] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = RightTriangleOperandType.RUN,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Run must be greater than 0"
                    else if (
                        run {
                            val diagonal = operands[RightTriangleOperandType.DIAGONAL]
                            diagonal?.let { diagonal <= input }?: false
                        })
                        "Run must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(RightTriangleOperandType.RUN)
                        numbers.remove(RightTriangleOperandType.RUN)
                    } else {
                        operands[RightTriangleOperandType.RUN] = validInput
                        numbers[RightTriangleOperandType.RUN] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = RightTriangleOperandType.DIAGONAL,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Diagonal must be greater than 0"
                    else if (
                        run {
                            val rise = operands[RightTriangleOperandType.RISE]
                            rise?.let { rise >= input }?: false
                        })
                        "Diagonal must be greater than the rise"
                    else if (
                        run {
                            val run = operands[RightTriangleOperandType.RUN]
                            run?.let { run >= input }?: false
                        })
                        "Diagonal must be greater than the run"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(RightTriangleOperandType.DIAGONAL)
                        numbers.remove(RightTriangleOperandType.DIAGONAL)
                    } else {
                        operands[RightTriangleOperandType.DIAGONAL] = validInput
                        numbers[RightTriangleOperandType.DIAGONAL] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = RightTriangleOperandType.TOP_ANGLE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO || input >= Deci(90))
                        "Top angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    operands.remove(RightTriangleOperandType.BOTTOM_ANGLE)
                    numbers.remove(RightTriangleOperandType.BOTTOM_ANGLE)
                    if (validInput == null) {
                        operands.remove(RightTriangleOperandType.TOP_ANGLE)
                        numbers.remove(RightTriangleOperandType.TOP_ANGLE)
                    } else {
                        operands[RightTriangleOperandType.TOP_ANGLE] = validInput
                        numbers[RightTriangleOperandType.TOP_ANGLE] = validInput
                        numbers[RightTriangleOperandType.BOTTOM_ANGLE] = Number(Deci(90).minus(validInput.toDeci()))
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = RightTriangleOperandType.BOTTOM_ANGLE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO || input >= Deci(90))
                        "Bottom angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    operands.remove(RightTriangleOperandType.TOP_ANGLE)
                    numbers.remove(RightTriangleOperandType.TOP_ANGLE)
                    if (validInput == null) {
                        operands.remove(RightTriangleOperandType.BOTTOM_ANGLE)
                        numbers.remove(RightTriangleOperandType.BOTTOM_ANGLE)
                    } else {
                        operands[RightTriangleOperandType.BOTTOM_ANGLE] = validInput
                        numbers[RightTriangleOperandType.BOTTOM_ANGLE] = validInput
                        numbers[RightTriangleOperandType.TOP_ANGLE] = Number(Deci(90) - validInput.toDeci())
                    }
                }
            )
        }
    }
}