package com.burtwelding.fabapp.ui.draw.right_triangle

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.OperandType
import com.burtwelding.fabapp.RightTriangleResetter
import com.burtwelding.fabapp.RightTriangleState
import com.burtwelding.fabapp.darken
import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.RightTriangle
import com.burtwelding.fabapp.storage.ItemHandle
import deci.trim
import org.kimplify.deci.Deci


@Composable
fun RightTriangleInfoPanel (modifier: Modifier,
                            programState: RightTriangleState,
                            resetter: RightTriangleResetter,
                            appState: AppState
) {
    @Composable
    fun InfoTileButton (
        modifier: Modifier,
        operandType: OperandType,
        validateFunction: (String) -> String,
        validInputFunction: (String) -> Unit
    ) {
        Box(
            modifier = modifier
                .background(operandType.color.darken(0.7f))
                .clickable (enabled = programState.operands.size < 2) {
                    appState.numpadOutputFunction = { validOutput ->
                        validInputFunction(validOutput)
                        if (programState.operands.size == 2) {
                            programState.rightTriangleCalculation = RightTriangle(
                                programState.operands.values.first(),
                                programState.operands.values.last()
                            ).calculate()
                            programState.runText = programState.rightTriangleCalculation.run.baseUnitDecimalValue.trim(3)
                            programState.riseText = programState.rightTriangleCalculation.rise.baseUnitDecimalValue.trim(3)
                            programState.diagonalText = programState.rightTriangleCalculation.diagonal.baseUnitDecimalValue.trim(3)
                            programState.topAngleText = programState.rightTriangleCalculation.topAngle.baseUnitDecimalValue.trim(3)
                            programState.bottomAngleText = programState.rightTriangleCalculation.bottomAngle.baseUnitDecimalValue.trim(3)
                        }
                    }
                    appState.numpadValidateFunction = validateFunction
                    appState.openNumpad = true
                }
        ) {
            Text(
                text = when (operandType) {
                    OperandType.RISE -> programState.riseText
                    OperandType.RUN -> programState.runText
                    OperandType.DIAGONAL -> programState.diagonalText
                    OperandType.TOP_ANGLE -> programState.topAngleText
                    OperandType.BOTTOM_ANGLE -> programState.bottomAngleText
                },
                modifier = Modifier
                    .align(Alignment.Center),
                color = Color.White
            )
        }
    }
    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight().weight(2f)
                .background(Color.Black)
                .clickable{ resetter.resetProgram++ }
        ) {
            Text(
                text = "RESET",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
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
                    if (input == "")
                        ""
                    else if (Deci(input) < Deci.ZERO)
                        "Rise be greater than 0"
                    else if (programState.operands[OperandType.DIAGONAL] != null && Deci(programState.diagonalText) < Deci(input))
                        "Rise must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    programState.riseText = validInput
                    if (validInput == "")
                        programState.operands.remove(OperandType.RISE)
                    else
                        programState.operands[OperandType.RISE] = RightTriangle.Operand.Rise(
                            ItemHandle<Length>(Length(Deci(validInput)))
                        )
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.RUN,
                validateFunction = { input ->
                    if (input == "")
                        ""
                    else if (Deci(input) < Deci.ZERO)
                        "Run be greater than 0"
                    else if (programState.operands[OperandType.DIAGONAL] != null && Deci(programState.diagonalText) < Deci(input))
                        "Run must be less than the diagonal"
                    else ""
                },
                validInputFunction = { validInput ->
                    programState.runText = validInput
                    if (validInput == "")
                        programState.operands.remove(OperandType.RUN)
                    else
                        programState.operands[OperandType.RUN] = RightTriangle.Operand.Run(
                            ItemHandle<Length>(Length(Deci(validInput)))
                        )
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.DIAGONAL,
                validateFunction = { input ->
                    if (input == "")
                        ""
                    else if (Deci(input) < Deci.ZERO)
                        "Diagonal be greater than 0"
                    else if (programState.operands[OperandType.RISE] != null && Deci(programState.riseText) > Deci(input))
                        "Diagonal must be greater than the rise"
                    else if (programState.operands[OperandType.RUN] != null && Deci(programState.runText) > Deci(input))
                        "Diagonal must be greater than the run"
                    else ""
                },
                validInputFunction = { validInput ->
                    programState.diagonalText = validInput
                    if (validInput == "")
                        programState.operands.remove(OperandType.DIAGONAL)
                    else
                        programState.operands[OperandType.DIAGONAL] = RightTriangle.Operand.Diagonal(
                            ItemHandle<Length>(Length(Deci(validInput)))
                        )
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.TOP_ANGLE,
                validateFunction = { input ->
                    if (input == "")
                        ""
                    else if (Deci(input) <= Deci.ZERO || Deci(input) >= Deci(90))
                        "Top angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    programState.operands.remove(OperandType.BOTTOM_ANGLE)
                    if (validInput == "") {
                        programState.topAngleText = ""
                        programState.bottomAngleText = ""
                        programState.operands.remove(OperandType.TOP_ANGLE)
                    } else {
                        programState.topAngleText = validInput
                        programState.bottomAngleText = Deci(90).minus(Deci(validInput)).toString()
                        programState.operands[OperandType.TOP_ANGLE] = RightTriangle.Operand.TopAngle(
                            ItemHandle<Angle>(Angle(Deci(validInput)))
                        )
                    }
                }
            )
            InfoTileButton(
                modifier = Modifier.fillMaxWidth().weight(1f),
                operandType = OperandType.BOTTOM_ANGLE,
                validateFunction = { input ->
                    if (input == "")
                        ""
                    else if (Deci(input) <= Deci.ZERO || Deci(input) >= Deci(90))
                        "Bottom angle must be greater than 0 and less than 90"
                    else ""
                },
                validInputFunction = { validInput ->
                    programState.operands.remove(OperandType.TOP_ANGLE)
                    if (validInput == "") {
                        programState.bottomAngleText = ""
                        programState.topAngleText = ""
                        programState.operands.remove(OperandType.BOTTOM_ANGLE)
                    } else {
                        programState.bottomAngleText = validInput
                        programState.topAngleText = (Deci(90).minus(Deci(validInput))).toString()
                        programState.operands[OperandType.BOTTOM_ANGLE] = RightTriangle.Operand.BottomAngle(
                            ItemHandle<Angle>(Angle(Deci(validInput)))
                        )
                    }
                }
            )
        }
    }
}