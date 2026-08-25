package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BromaLightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = TextOnAccent,
    primaryContainer = ActiveNavBg,
    onPrimaryContainer = RoyalBlue,
    secondary = SlateBlue,
    onSecondary = TextOnAccent,
    secondaryContainer = SecondaryBg,
    onSecondaryContainer = DeepNavy,
    tertiary = DeepNavy,
    onTertiary = TextOnAccent,
    background = LightGrayBg,
    onBackground = TextNavy,
    surface = CardWhite,
    onSurface = TextNavy,
    surfaceVariant = SecondaryBg,
    onSurfaceVariant = TextSlate,
    outline = BorderLight,
    outlineVariant = DividerColor,
    error = StatusError,
    onError = TextOnAccent
)

@Composable
fun BromaAcademyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Professional, High-Contrast Modern BROMA Academy Theme
    MaterialTheme(
        colorScheme = BromaLightColorScheme,
        typography = Typography,
        content = content
    )
}
