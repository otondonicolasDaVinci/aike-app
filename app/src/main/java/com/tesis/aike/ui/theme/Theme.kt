package com.tesis.aike.ui.theme

import android.app.Activity
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
    primary = VikingBlueNight,
    onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF335372),
    onPrimaryContainer = VikingBlueNight,

    secondary = PatagoniaGreenNight,
    onSecondary = Color(0xFF1E352B),
    secondaryContainer = Color(0xFF384F3F),
    onSecondaryContainer = PatagoniaGreenNight,

    tertiary = StoneGrayNight,
    onTertiary = Charcoal,
    background = DarkSurface,
    surface = DarkSurface,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = VikingBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlueContainer,
    onPrimaryContainer = Color(0xFF001E33),

    secondary = PatagoniaGreen,
    onSecondary = Color.White,
    secondaryContainer = LightGreenContainer,
    onSecondaryContainer = Color(0xFF0F1F12),

    tertiary = StoneGray,
    onTertiary = Charcoal,
    background = OffWhite,
    surface = OffWhite,
    onBackground = Charcoal,
    onSurface = Charcoal
)

@Composable
fun AikeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}