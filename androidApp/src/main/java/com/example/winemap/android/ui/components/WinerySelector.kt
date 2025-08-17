package com.example.winemap.android.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WinerySelector(
    selectedWinery: String,
    wineryList: List<String> = emptyList(),
    onWinerySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simple text field approach
    OutlinedTextField(
        value = selectedWinery,
        onValueChange = onWinerySelected,
        label = { Text("Choose winery") },
        placeholder = { Text("Enter or select winery name") },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.9f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray
        )
    )
}
