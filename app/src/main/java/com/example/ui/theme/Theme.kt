package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HarvestLeafGreen,
    onPrimary = Color.White,
    primaryContainer = HarvestForestDark,
    onPrimaryContainer = HarvestLightGreen,
    secondary = HarvestClay,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4E2611),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = HarvestSand,
    background = HarvestDarkBg,
    onBackground = HarvestDarkTextPrimary,
    surface = HarvestDarkSurface,
    onSurface = HarvestDarkTextPrimary,
    surfaceVariant = HarvestDarkCard,
    onSurfaceVariant = HarvestDarkTextSecondary,
    outline = Color(0xFF3F4B3F)
)

private val LightColorScheme = lightColorScheme(
    primary = HarvestForestGreen,
    onPrimary = Color.White,
    primaryContainer = HarvestMint,
    onPrimaryContainer = HarvestForestDark,
    secondary = HarvestClay,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCF),
    onSecondaryContainer = Color(0xFF381504),
    tertiary = HarvestLeafGreen,
    background = HarvestWarmCream,
    onBackground = TextPrimary,
    surface = HarvestSurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = HarvestCardBgLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
