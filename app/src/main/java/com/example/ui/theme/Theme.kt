package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CafeColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF031619),
    primaryContainer = CardDark,
    onPrimaryContainer = CyanGlow,
    secondary = GoldAccent,
    onSecondary = Color(0xFF1B1300),
    secondaryContainer = Color(0xFF2C2200),
    onSecondaryContainer = GoldLight,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun CafeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CafeColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CafeTheme(content = content)
}
