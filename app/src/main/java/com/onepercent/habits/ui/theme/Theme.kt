package com.onepercent.habits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OledDark = darkColorScheme(
    primary = Color(0xFF818CF8UL),
    onPrimary = Color(0xFF0F1020UL),
    secondary = Color(0xFFFBBF24UL),
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color(0xFF111111UL),
    onBackground = Color(0xFFF5F5F4UL),
    onSurface = Color(0xFFF5F5F4UL),
    onSurfaceVariant = Color(0xFFA3A3A3UL),
    outline = Color(0xFF1A1A1AUL),
    error = Color(0xFFFB7185UL),
)

private val Light = lightColorScheme(
    primary = Color(0xFF3730A3UL),
    onPrimary = Color(0xFFF8FAFCUL),
    secondary = Color(0xFFB45309UL),
    background = Color(0xFFE7E5E0UL),
    surface = Color(0xFFF6F5F2UL),
    surfaceVariant = Color(0xFFEFECE6UL),
    onBackground = Color(0xFF1C1917UL),
    onSurface = Color(0xFF1C1917UL),
    onSurfaceVariant = Color(0xFF78716CUL),
    outline = Color(0xFFE3DFD6UL),
    error = Color(0xFFBE123CUL),
)

@Composable
fun AppTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) OledDark else Light,
        content = content,
    )
}
