package com.example.winemap.android.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StarRating(
    rating: Int,
    maxRating: Int = 5,
    size: Dp = 20.dp,
    isInteractive: Boolean = false,
    onRatingChanged: (Int) -> Unit = {}
) {
    Row {
        repeat(maxRating) { index ->
            val isSelected = index < rating

            if (isInteractive) {
                IconButton(
                    onClick = { onRatingChanged(index + 1) },
                    modifier = Modifier.size(size + 8.dp)
                ) {
                    Text(
                        text = "⭐",
                        fontSize = (size.value * 0.8).sp,
                        color = if (isSelected) Color(0xFFFFD700) else Color.LightGray
                    )
                }
            } else {
                Text(
                    text = "⭐",
                    fontSize = (size.value * 0.8).sp,
                    color = if (isSelected) Color(0xFFFFD700) else Color.LightGray
                )
            }
        }
    }
}