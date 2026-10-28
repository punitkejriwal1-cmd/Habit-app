package com.onepercent.habits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OledDark = darkColorScheme(
    primary = Color(0xFF, 0x81, 0x8C, 0xF8),
    onPrimary = Color(0xFF, 0x0F, 0x10, 0x20),
    secondary = Color(0xFF, 0xFB, 0xBF, 0x24),
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color(0xFF, 0x11, 0x11, 0x11),
    onBackground = Color(0xFF, 0xF5, 0xF5, 0xF4),
    onSurface = Color(0xFF, 0xF5, 0xF5, 0xF4),
    onSurfaceVariant = Color(0xFF, 0xA3, 0xA3, 0xA3),
    outline = Color(0xFF, 0x1A, 0x1A, 0x1A),
    error = Color(0xFF, 0xFB, 0x71, 0x85),
)

private val Light = lightColorScheme(
    primary = Color(0xFF, 0x37, 0x30, 0xA3),
    onPrimary = Color(0xFF, 0xF8, 0xFA, 0xFC),
    secondary = Color(0xFF, 0xB4, 0x53, 0x09),
    background = Color(0xFF, 0xE7, 0xE5, 0xE0),
    surface = Color(0xFF, 0xF6, 0xF5, 0xF2),
    surfaceVariant = Color(0xFF, 0xEF, 0xEC, 0xE6),
    onBackground = Color(0xFF, 0x1C, 0x19, 0x17),
    onSurface = Color(0xFF, 0x1C, 0x19, 0x17),
    onSurfaceVariant = Color(0xFF, 0x78, 0x71, 0x6C),
    outline = Color(0xFF, 0xE3, 0xDF, 0xD6),
    error = Color(0xFF, 0xBE, 0x12, 0x3C),
)

private fun Color(a: Int, r: Int, g: Int, b: Int): Color =
    Color(a, r, g, b)

@Composable
fun OnePercentTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) OledDark else Light,
        content = content,
    )
}
