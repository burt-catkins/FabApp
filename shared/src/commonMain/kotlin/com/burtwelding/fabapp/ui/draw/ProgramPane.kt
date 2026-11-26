package com.burtwelding.fabapp.ui.draw

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.burtwelding.fabapp.AppState
import com.burtwelding.fabapp.Programs
import com.burtwelding.fabapp.darken
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
                       description: String,
                       color: Color
    ) {
        Box(
            Modifier.clickable { AppState.currentProgram = program },
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = description,
                colorFilter = ColorFilter.tint(
                    color = color,
                    blendMode = BlendMode.Modulate
                )
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
            description = "Right Triangle Calculator",
            color = Color.Red.darken(0.3f)
        )
        ProgramButton(
            program = Programs.MITER,
            icon = Res.drawable.icon_program_miter,
            description = "Miter Calculator",
            color = Color.Red.darken(0.3f)
        )
    }
}