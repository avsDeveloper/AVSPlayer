package com.example.avsplayer.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    surface = md_theme_dark_surface,
    onPrimary = md_theme_dark_onPrimary,
    onSurface = md_theme_dark_onSurface,
)

@Composable
fun AVSPlayerTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = ColorScheme


    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}