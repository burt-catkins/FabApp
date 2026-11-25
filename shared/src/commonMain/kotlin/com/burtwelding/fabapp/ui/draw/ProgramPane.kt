package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ProgramPane (modifier: Modifier) {
    @Composable
    fun ProgramButton (program: Programs,
                       icon: DrawableResource,
                       description: String) {
        Box(
            Modifier.clickable {
                AppState.openNumpad = false
                AppState.currentProgram = program
            },
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = description,
            )
        }
    }
    val spacing = 10.dp
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing)
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