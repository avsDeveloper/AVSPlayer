package com.example.avsplayer.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    secondary = md_theme_dark_secondary,
    tertiary = md_theme_dark_tertiary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onPrimary = md_theme_dark_onPrimary,
    onSecondary = md_theme_dark_onSecondary,
    onTertiary = md_theme_dark_onTertiary,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
)

@Composable
fun AVSPlayerTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = DarkColorScheme

//    val colors = if (!useDarkTheme) {
//        LightColors
//    } else {
//        DarkColors
//    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}