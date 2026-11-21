package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Programs
import fabapp.shared.generated.resources.Res
import fabapp.shared.generated.resources.icon_program_miter
import fabapp.shared.generated.resources.icon_program_righttriangle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProgramPane (modifier: Modifier, appState: AppState) {
    @Composable
    fun ProgramButton (program: Programs,
                       icon: DrawableResource,
                       description: String) {
        IconButton(
            onClick = {
                appState.openNumpad = false
                appState.currentProgram = program
            },
            modifier = Modifier.size(45.dp)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = description,
                modifier = Modifier.size(40.dp)
            )
        }
    }
    Column (
        modifier = modifier
            .background(Color.DarkGray)
            .fillMaxHeight()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProgramButton(
            program = Programs.RIGHT_TRIANGLE,
            icon = Res.drawable.icon_program_righttriangle,
            description = "Right Triangle Calculator"
        )
        ProgramButton(
            program = Programs.MITER,
            icon = Res.drawable.icon_program_miter,
            description = "Miter Calculator"
        )
    }
}