package com.mainhub.notepadpro.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4A847),
    secondary = Color(0xFFE8C97A),
    tertiary = Color(0xFF6DBF8A),
    background = Color(0xFF0F0E0C),
    surface = Color(0xFF1A1916),
    surfaceVariant = Color(0xFF242220),
    onPrimary = Color(0xFF0F0E0C),
    onBackground = Color(0xFFE8E4DC),
    onSurface = Color(0xFFE8E4DC),
    onSurfaceVariant = Color(0xFF9A9488),
    error = Color(0xFFE05C5C)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA67A12),
    secondary = Color(0xFF8A6410),
    tertiary = Color(0xFF6DBF8A),
    background = Color(0xFFF3F1EC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFECE9E2),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF201D17),
    onSurface = Color(0xFF201D17),
    onSurfaceVariant = Color(0xFF5D574A),
    error = Color(0xFFE05C5C)
)

@Composable
fun NotePadProTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
