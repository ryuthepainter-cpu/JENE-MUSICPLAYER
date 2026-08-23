package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val AuraDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = VioletAccent,
    background = DarkCharcoal,
    surface = GlassSurface,
    surfaceVariant = GlassSurfaceHigh,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun AuraMusicTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AuraDarkColorScheme,
        typography = Typography,
        content = content
    )
}
