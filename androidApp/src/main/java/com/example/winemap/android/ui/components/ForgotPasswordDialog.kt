package com.example.winemap.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ForgotPasswordDialog(
    email: String,
    isLoading: Boolean,
    message: String?,
    onEmailChange: (String) -> Unit,
    onSendReset: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (message != null) {
                    // Success State
                    ForgotPasswordSuccessContent(
                        message = message,
                        onDismiss = onDismiss
                    )
                } else {
                    // Input State
                    ForgotPasswordInputContent(
                        email = email,
                        isLoading = isLoading,
                        onEmailChange = onEmailChange,
                        onSendReset = onSendReset,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordInputContent(
    email: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSendReset: () -> Unit,
    onDismiss: () -> Unit
) {
    // Icon
    Icon(
        imageVector = Icons.Default.Email,
        contentDescription = null,
        modifier = Modifier
            .size(56.dp)
            .padding(bottom = 16.dp),
        tint = Color(0xFF6B5B73)
    )

    // Title
    Text(
        text = "Forgot Password?",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Description
    Text(
        text = "Enter your email and we'll send you a link to reset your password.",
        fontSize = 14.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // Email Field
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        enabled = !isLoading
    )

    // Buttons Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cancel Button
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Text(
                text = "Cancel",
                color = Color.Gray
            )
        }

        // Send Button
        Button(
            onClick = onSendReset,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B5B73)
            ),
            enabled = email.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Send",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordSuccessContent(
    message: String,
    onDismiss: () -> Unit
) {
    // Success Icon
    Text(
        text = "✅",
        fontSize = 56.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Title
    Text(
        text = "Email Sent!",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Message
    Text(
        text = message,
        fontSize = 14.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // OK Button
    Button(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6B5B73)
        )
    ) {
        Text(
            text = "OK",
            fontWeight = FontWeight.Medium
        )
    }
}