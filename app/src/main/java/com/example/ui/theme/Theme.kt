package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CarbonNoirColorScheme = darkColorScheme(
    primary = NoirAmber,
    secondary = BloodRed,
    tertiary = ClueGreen,
    background = CarbonDark,
    surface = CharcoalSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = GridWhite,
    onSurface = GridWhite,
    surfaceVariant = SlateCard,
    onSurfaceVariant = SlateGrey
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CarbonNoirColorScheme,
        typography = Typography,
        content = content
    )
}
