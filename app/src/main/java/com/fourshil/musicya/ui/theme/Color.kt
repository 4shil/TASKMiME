package com.fourshil.musicya.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
// Clean Minimalistic Color Palette
// Modern, neutral colors with subtle accent for a polished experience
// ═══════════════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────────────
// Base Colors
// ─────────────────────────────────────────────────────────────────────────────
val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)

// ─────────────────────────────────────────────────────────────────────────────
// Primary Accent - Modern Blue
// ─────────────────────────────────────────────────────────────────────────────
val AccentPrimary = Color(0xFF2563EB)        // Primary blue for light mode
val AccentPrimaryDark = Color(0xFF60A5FA)    // Lighter blue for dark mode
val AccentSecondary = Color(0xFF10B981)      // Green for success states
val AccentError = Color(0xFFEF4444)          // Red for errors
val AccentWarning = Color(0xFFF59E0B)        // Amber for warnings

// ─────────────────────────────────────────────────────────────────────────────
// Neutral Gray Scale - Clean and Modern
// ─────────────────────────────────────────────────────────────────────────────
val Gray50 = Color(0xFFFAFAFA)    // Lightest - backgrounds
val Gray100 = Color(0xFFF5F5F5)   // Light backgrounds
val Gray200 = Color(0xFFE5E5E5)   // Dividers, borders (light)
val Gray300 = Color(0xFFD4D4D4)   // Disabled states (light)
val Gray400 = Color(0xFFA3A3A3)   // Placeholder text
val Gray500 = Color(0xFF737373)   // Secondary text (light)
val Gray600 = Color(0xFF525252)   // Secondary text (dark)
val Gray700 = Color(0xFF404040)   // Primary text (dark)
val Gray800 = Color(0xFF262626)   // Dark surfaces
val Gray900 = Color(0xFF171717)   // Darkest - dark mode bg
val Gray950 = Color(0xFF0A0A0A)   // True dark

// ─────────────────────────────────────────────────────────────────────────────
// Semantic Colors - Light Mode
// ─────────────────────────────────────────────────────────────────────────────
val LightBackground = Gray50
val LightSurface = PureWhite
val LightSurfaceVariant = Gray100
val LightOnBackground = Gray900
val LightOnSurface = Gray900
val LightOnSurfaceVariant = Gray600
val LightOutline = Gray300
val LightOutlineVariant = Gray200

// ─────────────────────────────────────────────────────────────────────────────
// Claude Palette (Dark Mode)
// ─────────────────────────────────────────────────────────────────────────────
val ClaudeAccent = Color(0xFFD97757)      // Claude Orange/Toast
val ClaudeAccentLight = Color(0xFFE89A7D) // Lighter Orange
val ClaudeBackground = Color(0xFF131316)  // Deep Warm Black
val ClaudeSurface = Color(0xFF1F1F23)     // Interactive Surface
val ClaudeSurfaceVariant = Color(0xFF2E2E33) 
val ClaudeTextPrimary = Color(0xFFEDEDED) // Near White warmth
val ClaudeTextSecondary = Color(0xFFA1A1AA) // Zinc 400ish

// ─────────────────────────────────────────────────────────────────────────────
// Semantic Colors - Dark Mode
// ─────────────────────────────────────────────────────────────────────────────
val DarkBackground = ClaudeBackground
val DarkSurface = ClaudeSurface
val DarkSurfaceVariant = ClaudeSurfaceVariant
val DarkOnBackground = ClaudeTextPrimary
val DarkOnSurface = ClaudeTextPrimary
val DarkOnSurfaceVariant = ClaudeTextSecondary
val DarkOutline = Color(0xFF3F3F46)       // Zinc 700
val DarkOutlineVariant = Color(0xFF27272A)

// ─────────────────────────────────────────────────────────────────────────────
// Progress/Player Accent
// ─────────────────────────────────────────────────────────────────────────────
val ProgressColor = AccentPrimary
val ProgressColorDark = ClaudeAccent

// ─────────────────────────────────────────────────────────────────────────────
// Legacy Aliases (for backward compatibility during migration)
// ─────────────────────────────────────────────────────────────────────────────
val NeoCoral = AccentPrimary
val NeoCoralLight = AccentPrimaryDark
val NeoCoralDark = AccentError
val NeoAmber = AccentWarning
val NeoTeal = AccentSecondary
val MangaRed = AccentPrimary
val MustardYellow = AccentWarning
val AccentRed = AccentError

// Slate → Gray migration aliases
val Slate50 = Gray50
val Slate100 = Gray100
val Slate200 = Gray200
val Slate300 = Gray300
val Slate400 = Gray400
val Slate500 = Gray500
val Slate600 = Gray600
val Slate700 = Gray700
val Slate800 = Gray800
val Slate900 = Gray900
val Slate950 = Gray950

val OffWhite = Gray50
val OffBlack = Gray900
val NeoShadowLight = Gray700
val NeoShadowDark = Gray950
val NeoBorderLight = Gray300
val NeoBorderDark = Gray700
val PrimaryText = Gray900
val DarkPrimaryText = Gray50

// ─────────────────────────────────────────────────────────────────────────────
// Neobrutalism Palette
// ─────────────────────────────────────────────────────────────────────────────
val NeoPrimary = Color(0xFF3B82F6)        // #3B82F6
val NeoBackground = Color(0xFFFDFCF7)     // #FDFCF7
val NeoPink = Color(0xFFFFB7D5)           // #FFB7D5
val NeoYellow = Color(0xFFFFE999)         // #FFE999
val NeoGreen = Color(0xFFB2F0D1)          // #B2F0D1
val NeoBlue = Color(0xFFA5C9FF)           // #A5C9FF (Accent Blue)
val NeoBorder = Color(0xFF000000)         // Pure Black Border

// Zinc aliases
val Zinc50 = Gray50
val Zinc100 = Gray100
val Zinc200 = Gray200
val Zinc300 = Gray300
val Zinc700 = Gray700
val Zinc800 = Gray800
val Zinc900 = Gray900
val Zinc950 = Gray950

// Additional legacy aliases
val NeoAmberDark = AccentWarning
val NeoAmberLight = Color(0xFFFDE68A)
val NeoSage = AccentSecondary
val NeoLavender = Color(0xFFC4B5FD)
val NeoSky = Color(0xFF7DD3FC)
val NeoTealDark = Color(0xFF14B8A6)
