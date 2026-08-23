package com.jene.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JeneDarkColorScheme = darkColorScheme(
    primary = JeneAccentBlue,
    secondary = JeneAccentViolet,
    background = JeneBackground,
    surface = JeneGlassSurface,
    surfaceVariant = JeneGlassSurfaceHigh,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = JeneTextPrimary,
    onSurface = JeneTextPrimary,
    onSurfaceVariant = JeneTextSecondary,
    outline = JeneGlassBorder
)

@Composable
fun JeneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JeneDarkColorScheme,
        typography = Typography,
        content = content
    )
}
