package com.fourshil.musicya.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════════════════
// Soft Neo-Brutalism Light Theme with Claude Palette
// Warm, approachable colors with Claude Orange (#D97757) as primary accent
// ═══════════════════════════════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    // Primary - Claude Orange
    primary = ClaudeOrange,
    onPrimary = PureWhite,
    primaryContainer = ClaudeOrangeLight.copy(alpha = 0.20f),
    onPrimaryContainer = ClaudeOrangeDark,
    
    // Secondary - Soft Green
    secondary = NeoPastelGreen,
    onSecondary = Warm900,
    secondaryContainer = NeoPastelGreen.copy(alpha = 0.20f),
    onSecondaryContainer = Warm800,
    
    // Tertiary - Soft Blue
    tertiary = NeoPastelBlue,
    onTertiary = Warm900,
    tertiaryContainer = NeoPastelBlue.copy(alpha = 0.20f),
    onTertiaryContainer = Warm800,
    
    // Background & Surface - Warm grays
    background = Warm50,
    onBackground = Warm900,
    surface = PureWhite,
    onSurface = Warm900,
    surfaceVariant = Warm100,
    onSurfaceVariant = Warm700,
    
    // Outline - Soft borders
    outline = Warm400,
    outlineVariant = Warm200,
    
    // Error
    error = NeoError,
    onError = PureWhite,
    errorContainer = NeoError.copy(alpha = 0.15f),
    onErrorContainer = NeoError,
    
    // Inverse
    inverseSurface = Warm800,
    inverseOnSurface = Warm100,
    inversePrimary = ClaudeOrangeLight,
    
    // Scrim
    scrim = Warm900.copy(alpha = 0.32f)
)

// ═══════════════════════════════════════════════════════════════════════════════
// Soft Neo-Brutalism Dark Theme with Claude Palette
// Deep charcoal backgrounds with Claude Orange accent for warmth
// ═══════════════════════════════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    // Primary - Claude Orange
    primary = ClaudeOrange,
    onPrimary = PureWhite,
    primaryContainer = ClaudeOrange.copy(alpha = 0.25f),
    onPrimaryContainer = ClaudeOrangeLight,
    
    // Secondary - Soft Green
    secondary = NeoPastelGreen,
    onSecondary = Warm900,
    secondaryContainer = NeoPastelGreen.copy(alpha = 0.20f),
    onSecondaryContainer = NeoPastelGreen,
    
    // Tertiary - Soft Blue
    tertiary = NeoPastelBlue,
    onTertiary = Warm900,
    tertiaryContainer = NeoPastelBlue.copy(alpha = 0.20f),
    onTertiaryContainer = NeoPastelBlue,
    
    // Background & Surface - Claude Dark palette
    background = ClaudeBackground,
    onBackground = ClaudeTextPrimary,
    surface = ClaudeSurface,
    onSurface = ClaudeTextPrimary,
    surfaceVariant = ClaudeSurfaceElevated,
    onSurfaceVariant = ClaudeTextSecondary,
    
    // Outline - Soft dark borders
    outline = ClaudeBorder,
    outlineVariant = ClaudeBorderSubtle,
    
    // Error
    error = NeoError,
    onError = PureWhite,
    errorContainer = NeoError.copy(alpha = 0.20f),
    onErrorContainer = NeoError,
    
    // Inverse
    inverseSurface = Warm100,
    inverseOnSurface = Warm800,
    inversePrimary = ClaudeOrangeDark,
    
    // Scrim
    scrim = PureBlack.copy(alpha = 0.70f)
)

@Composable
fun MusicyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // DISABLED to enforce our clean design
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
