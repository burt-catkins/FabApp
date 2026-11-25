package com.burtwelding.fabapp.programs.right_triangle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Fraction
import com.burtwelding.fabapp.ImperialFraction
import com.burtwelding.fabapp.darken
import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.RightTriangle
import com.burtwelding.fabapp.storage.ItemHandle
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
fun RightTriangleInfoPanel (
    modifier: Modifier
) {
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
            modifier
                .clickable { action() },
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
        operandType: OperandType,
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
                            AppState.numpadOutputFunction = { validOutput ->
                                validInputFunction(validOutput)
                                if (operands.size == 2) {
                                    val operandKeys = operands.keys
                                    val firstOperandType = operandKeys.first()
                                    val secondOperandType = operandKeys.last()
                                    fun getOperand (operandType: OperandType) =
                                        when (operandType) {
                                            OperandType.RISE -> RightTriangle.Operand.Rise(ItemHandle<Length>(Length(operands[operandType]?.toDeci()?: error("bruh the operand type is rise but there aint no number"))))
                                            OperandType.RUN -> RightTriangle.Operand.Run(ItemHandle<Length>(Length(operands[operandType]?.toDeci()?: error("bruh the operand type is run but there aint no number"))))
                                            OperandType.DIAGONAL -> RightTriangle.Operand.Diagonal(ItemHandle<Length>(Length(operands[operandType]?.toDeci()?: error("bruh the operand type is diagonal but there aint no number"))))
                                            OperandType.TOP_ANGLE -> RightTriangle.Operand.TopAngle(ItemHandle<Angle>(Angle(operands[operandType]?.toDeci()?: error("bruh the operand type is top angle but there aint no number"))))
                                            OperandType.BOTTOM_ANGLE -> RightTriangle.Operand.BottomAngle(ItemHandle<Angle>(Angle(operands[operandType]?.toDeci()?: error("bruh the operand type is bottom angle but there aint no number"))))
                                        }
                                    val calculation = RightTriangle(
                                        getOperand(firstOperandType),
                                        getOperand(secondOperandType)
                                    ).calculate()
                                    RightTriangleData.rightTriangleCalculation = calculation
                                    numbers[OperandType.RISE] = Number(calculation[OperandType.RISE]?.baseUnitDecimalValue?: error("yoooooooo the rise calculation is null!!"))
                                    numbers[OperandType.RUN] = Number(calculation[OperandType.RUN]?.baseUnitDecimalValue?: error("yoooooooo the RUN calculation is null!!"))
                                    numbers[OperandType.DIAGONAL] = Number(calculation[OperandType.DIAGONAL]?.baseUnitDecimalValue?: error("yoooooooo the DIAGONAL calculation is null!!"))
                                    numbers[OperandType.TOP_ANGLE] = Number(calculation[OperandType.TOP_ANGLE]?.baseUnitDecimalValue?: error("yoooooooo the TOP_ANGLE calculation is null!!"))
                                    numbers[OperandType.BOTTOM_ANGLE] = Number(calculation[OperandType.BOTTOM_ANGLE]?.baseUnitDecimalValue?: error("yoooooooo the BOTTOM_ANGLE calculation is null!!"))
                                }
                            }
                            AppState.numpadValidateFunction = validateFunction
                            AppState.openNumpad = true
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box (
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
                                autoSize = TextAutoSize.StepBased(),
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
                        NumberText(
                            number = numbers[operandType],
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxHeight(),
                            fontColor = Color.White,
                            fontSizeHeightWeight = 0.65f,
                            maxDenominator = when (operandType) {
                                OperandType.RISE, OperandType.RUN, OperandType.DIAGONAL -> maxDenominator.denominator
                                OperandType.TOP_ANGLE, OperandType.BOTTOM_ANGLE -> null
                            },
                            decimalPrecision = 1
                        )
                    }
                }
            }
        )
    }
    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(
                modifier = Modifier
                    .weight(1f),
                color = Color.DarkGray.darken(0.2f),
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
                    if (maxDenominator == ImperialFraction.WHOLE) {
                        Text(
                            text = "WHOLE",
                            color = Color.Black,
                            fontSize = 30.sp
                        )
                    } else {
                        NumberText(
                            Number(null, Fraction(Deci.ONE, maxDenominator.denominator)),
                            modifier = Modifier
                                .fillMaxHeight(0.3f),
                            fontColor = Color.Black,
                            fontSizeHeightWeight = 1f,
                            maxDenominator = maxDenominator.denominator
                        )
                    }
                }
            )
        }
        Column (
            modifier = Modifier
                .fillMaxHeight().weight(5f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.RISE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Rise be greater than 0"
                    else if (
                        run {
                            val diagonal = operands[OperandType.DIAGONAL]
                            diagonal?.let { diagonal <= input }?: false
                        })
                        "Rise must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(OperandType.RISE)
                        numbers.remove(OperandType.RISE)
                    } else {
                        operands[OperandType.RISE] = validInput
                        numbers[OperandType.RISE] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.RUN,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Run be greater than 0"
                    else if (
                        run {
                            val diagonal = operands[OperandType.DIAGONAL]
                            diagonal?.let { diagonal <= input }?: false
                        })
                        "Run must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(OperandType.RUN)
                        numbers.remove(OperandType.RUN)
                    } else {
                        operands[OperandType.RUN] = validInput
                        numbers[OperandType.RUN] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.DIAGONAL,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO)
                        "Diagonal be greater than 0"
                    else if (
                        run {
                            val rise = operands[OperandType.RISE]
                            rise?.let { rise >= input }?: false
                        })
                        "Diagonal must be greater than the rise"
                    else if (
                        run {
                            val run = operands[OperandType.RUN]
                            run?.let { run >= input }?: false
                        })
                        "Diagonal must be greater than the run"
                    else ""
                },
                validInputFunction = { validInput ->
                    if (validInput == null) {
                        operands.remove(OperandType.DIAGONAL)
                        numbers.remove(OperandType.DIAGONAL)
                    } else {
                        operands[OperandType.DIAGONAL] = validInput
                        numbers[OperandType.DIAGONAL] = validInput
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.TOP_ANGLE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO || input >= Deci(90))
                        "Top angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    operands.remove(OperandType.BOTTOM_ANGLE)
                    numbers.remove(OperandType.BOTTOM_ANGLE)
                    if (validInput == null) {
                        operands.remove(OperandType.TOP_ANGLE)
                        numbers.remove(OperandType.TOP_ANGLE)
                    } else {
                        operands[OperandType.TOP_ANGLE] = validInput
                        numbers[OperandType.TOP_ANGLE] = validInput
                        numbers[OperandType.BOTTOM_ANGLE] = Number(Deci(90).minus(validInput.toDeci()))
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.BOTTOM_ANGLE,
                validateFunction = { input ->
                    if (input == null)
                        ""
                    else if (input <= Deci.ZERO || input >= Deci(90))
                        "Bottom angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    operands.remove(OperandType.TOP_ANGLE)
                    numbers.remove(OperandType.TOP_ANGLE)
                    if (validInput == null) {
                        operands.remove(OperandType.BOTTOM_ANGLE)
                        numbers.remove(OperandType.BOTTOM_ANGLE)
                    } else {
                        operands[OperandType.BOTTOM_ANGLE] = validInput
                        numbers[OperandType.BOTTOM_ANGLE] = validInput
                        numbers[OperandType.TOP_ANGLE] = Number(Deci(90) - validInput.toDeci())
                    }
                }
            )
        }
    }
}