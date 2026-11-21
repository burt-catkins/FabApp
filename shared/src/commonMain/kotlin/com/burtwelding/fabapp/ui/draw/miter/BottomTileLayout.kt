//
//package com.burtwelding.fabapp.ui.draw.miter
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.burtwelding.fabapp.ui.draw.Tile
//import com.burtwelding.fabapp.ui.draw.TileButton
//@Composable
//fun BottomTileLayout(
//    modifier: Modifier = Modifier.Companion
//) {
//    val spacing = 6.dp
//
//    Column(
//        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(spacing)
//    ) {
//
//        // First two rows, with a tall tile on the left
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(2f),
//            horizontalArrangement = Arrangement.spacedBy(spacing)
//        ) {
//
//            // Tall tile (spans two rows)
//            Tile(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight(),
//                text = "Joint Angle:\n$totalJointAngleString°",
//                pieceSide = null
//            )
//
//            // Middle and right columns
//            Column(
//                modifier = Modifier.weight(2f),
//                verticalArrangement = Arrangement.spacedBy(spacing)
//            ) {
//
//                Row(
//                    modifier = Modifier.weight(1f),
//                    horizontalArrangement = Arrangement.spacedBy(spacing)
//                ) {
//                    Tile(
//                        modifier = Modifier.weight(1f).fillMaxHeight(),
//                        text = "Size:\n$leftPieceSizeString",
//                        pieceSide = Side.LEFT
//                    )
//
//                    Tile(
//                        modifier = Modifier.weight(1f).fillMaxHeight(),
//                        text = "Size:\n$rightPieceSizeString",
//                        pieceSide = Side.RIGHT
//                    )
//                }
//
//                Row(
//                    modifier = Modifier.weight(1f),
//                    horizontalArrangement = Arrangement.spacedBy(spacing)
//                ) {
//                    Tile(
//                        modifier = Modifier.weight(1f).fillMaxHeight(),
//                        text = "Miter Angle:\n$leftMiterAngleString°",
//                        pieceSide = Side.LEFT
//                    )
//
//                    Tile(
//                        modifier = Modifier.weight(1f).fillMaxHeight(),
//                        text = "Miter Angle:\n$rightMiterAngleString°",
//                        pieceSide = Side.RIGHT
//                    )
//                }
//            }
//        }
//
//        // Third row
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            horizontalArrangement = Arrangement.spacedBy(spacing)
//        ) {
//            TileButton(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = "New",
//                onClick = { showMiterDialog = true }
//            )
//            Tile(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = "Miter Rise:\n$leftMiterRiseString",
//                pieceSide = Side.LEFT
//            )
//            Tile(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = "Miter Rise:\n$rightMiterRiseString",
//                pieceSide = Side.RIGHT
//            )
//        }
//
//        // Fourth row
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            horizontalArrangement = Arrangement.spacedBy(spacing)
//        ) {
//            TileButton(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = numberTypeSelected.name.lowercase().replaceFirstChar { it.uppercase() },
//                onClick = {
//                    numberTypeSelected = when (numberTypeSelected) {
//                        NumberTypeSelected.DECIMAL -> NumberTypeSelected.FRACTION
//                        NumberTypeSelected.FRACTION -> NumberTypeSelected.DECIMAL
//                    }
//                }
//            )
//            Tile(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = "Angle Side:\n$leftMiterAngleSideString",
//                pieceSide = Side.LEFT
//            )
//            Tile(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                text = "Angle Side:\n$rightMiterAngleSideString",
//                pieceSide = Side.RIGHT
//            )
//        }
//    }
//}