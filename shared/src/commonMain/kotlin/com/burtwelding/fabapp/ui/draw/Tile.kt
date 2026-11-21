//package com.burtwelding.fabapp.ui.draw
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.burtwelding.fabapp.Side
//
//@Composable
//fun Tile(
//    modifier: Modifier = Modifier.Companion,
//    text: String,
//    pieceSide: Side?
//) {
//    Box(
//        modifier = modifier
//            .background(
//                color = when (pieceSide) {
//                    Side.LEFT -> Color(red = 0.8f, green = 0.4f, blue = 0.4f)
//                    Side.RIGHT -> Color(red = 0.4f, green = 0.4f, blue = 0.8f)
//                    null -> Color(red = 0.6f, green = 0.3f, blue = 0.9f)
//                },
//                shape = RoundedCornerShape(8.dp)
//            )
//            .border(
//                width = 1.dp,
//                color = Color.DarkGray,
//                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
//            )
//            .padding(4.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            textAlign = TextAlign.Center
//        )
//    }
//}