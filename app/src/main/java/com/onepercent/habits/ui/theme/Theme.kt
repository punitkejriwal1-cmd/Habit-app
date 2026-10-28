package com.onepercent.habits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OledDark = darkColorScheme(
    primary = Color(0xFF818CF8L),
    onPrimary = Color(0xFF0F1020L),
    secondary = Color(0xFFFBBF24L),
    background = Color(0xFF000000L),
    surface = Color(0xFF000000L),
    surfaceVariant = Color(0xFF111111L),
    onBackground = Color(0xFFF5F5F4L),
    onSurface = Color(0xFFF5F5F4L),
    onSurfaceVariant = Color(0xFFA3A3A3L),
    outline = Color(0xFF1A1A1AL),
    error = Color(0xFFFB7185L),
)

private val Light = lightColorScheme(
    primary = Color(0xFF3730A3L),
    onPrimary = Color(0xFFF8FAFCL),
    secondary = Color(0xFFB45309L),
    background = Color(0xFFE7E5E0L),
    surface = Color(0xFFF6F5F2L),
    surfaceVariant = Color(0xFFEFECE6L),
    onBackground = Color(0xFF1C1917L),
    onSurface = Color(0xFF1C1917L),
    onSurfaceVariant = Color(0xFF78716CL),
    outline = Color(0xFFE3DFD6L),
    error = Color(0xFFBE123CL),
)

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
