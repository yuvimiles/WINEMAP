package com.example.winemap.android.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Winemap Colors
val WineRed = Color(0xFF8B0000)
val WineRedLight = Color(0xFFBD5555)
val WineRedDark = Color(0xFF5D0000)
val WineGold = Color(0xFFD4AF37)
val WineGoldLight = Color(0xFFE6C659)
val WineBackground = Color(0xFFF5F5DC)
val WineCardBackground = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = WineRed,
    secondary = WineGold,
    tertiary = WineRedLight,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2B2930),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

private val LightColorScheme = lightColorScheme(
    primary = WineRed,
    secondary = WineGold,
    tertiary = WineRedLight,
    background = WineBackground,
    surface = WineCardBackground,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun WinemapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}