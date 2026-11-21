//package com.burtwelding.fabapp.ui.draw
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun TileButton(
//    modifier: Modifier = Modifier.Companion,
//    text: String,
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        modifier = modifier,
//        shape = RoundedCornerShape(8.dp),
//        contentPadding = PaddingValues(4.dp),
//        colors = buttonColor
//    ) {
//        Text(
//            text = text,
//            textAlign = TextAlign.Center
//        )
//    }
//}