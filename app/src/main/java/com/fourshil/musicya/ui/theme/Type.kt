package com.fourshil.musicya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// We are mimicking "Archivo Black" and "Space Grotesk" using system fonts
// Serif + Black Weight ~ Archivo Black (Display)
// Monospace/Sans + Medium ~ Space Grotesk (Body)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Black,
        fontSize = 72.sp,
        lineHeight = 64.sp,
        letterSpacing = (-2).sp // Relaxed from -4
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp,
        lineHeight = 44.sp,
        letterSpacing = (-1).sp // Relaxed from -2
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp,
         letterSpacing = (-0.5).sp // Relaxed from -1
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Used for buttons/tabs
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 10.sp,
        letterSpacing = 2.sp // Widely spaced small caps
    )
)
