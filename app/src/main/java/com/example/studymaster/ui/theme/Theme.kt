package com.example.studymaster.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF667eea),
    secondary = Color(0xFF764ba2),
    tertiary = Color(0xFF48bb78),
    background = Color(0xFFF7FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2D3748),
    onSurface = Color(0xFF2D3748)
)

@Composable
fun StudyMasterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}