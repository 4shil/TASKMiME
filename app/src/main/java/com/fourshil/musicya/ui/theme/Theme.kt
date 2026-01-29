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
// Clean Minimalistic Light Theme
// ═══════════════════════════════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    // Primary
    primary = AccentPrimary,
    onPrimary = PureWhite,
    primaryContainer = AccentPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = AccentPrimary,
    
    // Secondary
    secondary = AccentSecondary,
    onSecondary = PureWhite,
    secondaryContainer = AccentSecondary.copy(alpha = 0.12f),
    onSecondaryContainer = AccentSecondary,
    
    // Tertiary
    tertiary = AccentWarning,
    onTertiary = PureWhite,
    tertiaryContainer = AccentWarning.copy(alpha = 0.12f),
    onTertiaryContainer = AccentWarning,
    
    // Background & Surface
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    
    // Outline
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    
    // Error
    error = AccentError,
    onError = PureWhite,
    errorContainer = AccentError.copy(alpha = 0.12f),
    onErrorContainer = AccentError,
    
    // Inverse
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = AccentPrimaryDark,
    
    // Scrim
    scrim = PureBlack.copy(alpha = 0.32f)
)

// ═══════════════════════════════════════════════════════════════════════════════
// Clean Minimalistic Dark Theme (Claude Palette)
// ═══════════════════════════════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = ClaudeAccent,
    onPrimary = PureWhite, // Orange on White or White on Orange? White on Orange is cleaner.
    primaryContainer = ClaudeAccent.copy(alpha = 0.2f),
    onPrimaryContainer = ClaudeAccentLight,
    
    // Secondary
    secondary = AccentSecondary,
    onSecondary = Gray900,
    secondaryContainer = AccentSecondary.copy(alpha = 0.16f),
    onSecondaryContainer = AccentSecondary,
    
    // Tertiary
    tertiary = AccentWarning,
    onTertiary = Gray900,
    tertiaryContainer = AccentWarning.copy(alpha = 0.16f),
    onTertiaryContainer = AccentWarning,
    
    // Background & Surface
    background = DarkBackground,       // ClaudeBackground
    onBackground = DarkOnBackground,   // ClaudeTextPrimary
    surface = DarkSurface,             // ClaudeSurface
    onSurface = DarkOnSurface,         // ClaudeTextPrimary
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    
    // Outline
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    
    // Error
    error = AccentError,
    onError = Gray900,
    errorContainer = AccentError.copy(alpha = 0.16f),
    onErrorContainer = AccentError,
    
    // Inverse
    inverseSurface = Gray100,
    inverseOnSurface = Gray800,
    inversePrimary = AccentPrimary,
    
    // Scrim
    scrim = PureBlack.copy(alpha = 0.7f)
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
