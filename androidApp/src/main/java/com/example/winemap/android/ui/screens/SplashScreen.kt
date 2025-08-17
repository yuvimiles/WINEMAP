package com.example.winemap.android.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winemap.R


@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // White Circle with Content
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.Center)
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = CircleShape
                )
                .scale(scale.value),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(40.dp)
            ) {
                // Wine Logo
                Image(
                    painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                    contentDescription = "Winemap Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .offset(y = (-30).dp)

                )



                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The easy way to\ndiscover, rate and\nshare experiences\nfrom all wineries in\nIsrael",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp

                )

                Spacer(modifier = Modifier.height(16.dp))

                // LET'S GO Button
                Button(
                    onClick = onNavigateToAuth,
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B5B73)
                    )
                ) {
                    Text(
                        text = "LET'S GO !",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}