package com.fourshil.musicya.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
// Soft Neo-Brutalism Color Palette with Claude Theme
// Warm, approachable colors with bold accents
// ═══════════════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────────────
// Claude Accent Colors - The Heart of the Theme
// ─────────────────────────────────────────────────────────────────────────────
val ClaudeOrange = Color(0xFFD97757)          // Primary accent - Claude's signature
val ClaudeOrangeLight = Color(0xFFE89A7D)     // Lighter variant for containers
val ClaudeOrangeDark = Color(0xFFC4593A)      // Darker variant for pressed states
val ClaudeOrangeSubtle = Color(0xFFFFF4F0)    // Very subtle background tint (light)
val ClaudeOrangeSubtleDark = Color(0xFF2A1F1C) // Very subtle background tint (dark)

// ─────────────────────────────────────────────────────────────────────────────
// Semantic Accent Colors
// ─────────────────────────────────────────────────────────────────────────────
val SemanticSuccess = Color(0xFF10B981)       // Green - favorites, success
val SemanticSuccessLight = Color(0xFF34D399)  // Light green
val SemanticError = Color(0xFFEF4444)         // Red - errors, delete
val SemanticErrorLight = Color(0xFFFCA5A5)    // Light red
val SemanticWarning = Color(0xFFF59E0B)       // Amber - warnings
val SemanticInfo = Color(0xFF3B82F6)          // Blue - info states

// ─────────────────────────────────────────────────────────────────────────────
// Neutral Scale - Warm Grays for Light Mode
// ─────────────────────────────────────────────────────────────────────────────
val Warm50 = Color(0xFFFAFAF9)    // Warmest white - backgrounds
val Warm100 = Color(0xFFF5F5F4)   // Light backgrounds
val Warm200 = Color(0xFFE7E5E4)   // Dividers, borders (light)
val Warm300 = Color(0xFFD6D3D1)   // Disabled states, subtle borders
val Warm400 = Color(0xFFA8A29E)   // Placeholder text
val Warm500 = Color(0xFF78716C)   // Secondary text (light)
val Warm600 = Color(0xFF57534E)   // Secondary text emphasis
val Warm700 = Color(0xFF44403C)   // Primary text (dark surfaces)
val Warm800 = Color(0xFF292524)   // Dark surfaces
val Warm900 = Color(0xFF1C1917)   // Darkest - near black

// ─────────────────────────────────────────────────────────────────────────────
// Claude Dark Mode Palette - Warm, Cozy Dark Theme
// ─────────────────────────────────────────────────────────────────────────────
val ClaudeBackground = Color(0xFF1A1A1D)      // Deep warm black
val ClaudeSurface = Color(0xFF232326)         // Card/elevated surfaces
val ClaudeSurfaceHigh = Color(0xFF2C2C30)     // Higher elevation
val ClaudeSurfaceVariant = Color(0xFF37373D)  // Interactive elements bg
val ClaudeTextPrimary = Color(0xFFF4F4F5)     // Primary text
val ClaudeTextSecondary = Color(0xFFA1A1AA)   // Secondary text
val ClaudeTextMuted = Color(0xFF71717A)       // Muted/disabled text
val ClaudeBorder = Color(0xFF3F3F46)          // Soft borders
val ClaudeBorderSubtle = Color(0xFF27272A)    // Very subtle borders

// ─────────────────────────────────────────────────────────────────────────────
// Light Mode Semantic Mappings
// ─────────────────────────────────────────────────────────────────────────────
val LightBackground = Warm50
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Warm100
val LightOnBackground = Warm900
val LightOnSurface = Warm900
val LightOnSurfaceVariant = Warm600
val LightOutline = Warm300
val LightOutlineVariant = Warm200

// ─────────────────────────────────────────────────────────────────────────────
// Dark Mode Semantic Mappings
// ─────────────────────────────────────────────────────────────────────────────
val DarkBackground = ClaudeBackground
val DarkSurface = ClaudeSurface
val DarkSurfaceVariant = ClaudeSurfaceVariant
val DarkOnBackground = ClaudeTextPrimary
val DarkOnSurface = ClaudeTextPrimary
val DarkOnSurfaceVariant = ClaudeTextSecondary
val DarkOutline = ClaudeBorder
val DarkOutlineVariant = ClaudeBorderSubtle

// ─────────────────────────────────────────────────────────────────────────────
// Soft Neo-Brutalism - Shadows & Accents
// Softer than traditional neo-brutalism, but still bold
// ─────────────────────────────────────────────────────────────────────────────
val SoftShadowLight = Warm800.copy(alpha = 0.12f)   // Soft shadow for light mode
val SoftShadowDark = Color.Black.copy(alpha = 0.5f) // Soft shadow for dark mode

// Soft borders for neo-brutalism
val SoftBorderLight = Warm400                         // Soft border for light mode
val SoftBorderDark = ClaudeBorder                     // Soft border for dark mode

// Neo-brutalist accent colors (pastel variants)
val NeoPastelPink = Color(0xFFFFD4E5)         // Soft pink - favorites
val NeoPastelYellow = Color(0xFFFFF3CD)       // Soft yellow
val NeoPastelGreen = Color(0xFFD1FAE5)        // Soft green
val NeoPastelBlue = Color(0xFFDBEAFE)         // Soft blue
val NeoPastelOrange = Color(0xFFFFEDD5)       // Soft orange (Claude tint)

// Error color
val NeoError = SemanticError

// Missing surface variant
val ClaudeSurfaceElevated = ClaudeSurfaceHigh

// ─────────────────────────────────────────────────────────────────────────────
// Legacy Aliases - For Backward Compatibility
// ─────────────────────────────────────────────────────────────────────────────
val ClaudeAccent = ClaudeOrange
val ClaudeAccentLight = ClaudeOrangeLight
val AccentPrimary = ClaudeOrange
val AccentPrimaryDark = ClaudeOrangeLight
val AccentSecondary = SemanticSuccess
val AccentError = SemanticError
val AccentWarning = SemanticWarning

// Gray aliases
val Gray50 = Warm50
val Gray100 = Warm100
val Gray200 = Warm200
val Gray300 = Warm300
val Gray400 = Warm400
val Gray500 = Warm500
val Gray600 = Warm600
val Gray700 = Warm700
val Gray800 = Warm800
val Gray900 = Warm900
val Gray950 = Color(0xFF0C0A09)
val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)

// Neo colors → Semantic mappings
val NeoCoral = ClaudeOrange
val NeoCoralLight = ClaudeOrangeLight
val NeoPink = NeoPastelPink
val NeoGreen = NeoPastelGreen
val NeoBlue = NeoPastelBlue
val NeoYellow = NeoPastelYellow
val NeoViolet = Color(0xFFDDD6FE)             // Soft violet
val NeoLavender = Color(0xFFE9D5FF)           // Soft lavender
val NeoBorder = ClaudeBorder
val NeoBackground = LightBackground
val NeoPrimary = ClaudeOrange
val NeoTeal = SemanticSuccess
val NeoAmber = SemanticWarning
val MangaRed = SemanticError

// Compatibility shims
val ProgressColor = ClaudeOrange
val ProgressColorDark = ClaudeOrange
val OffWhite = Warm50
val OffBlack = Warm900
val NeoShadowLight = SoftShadowLight
val NeoShadowDark = SoftShadowDark
val NeoBorderLight = Warm300
val NeoBorderDark = ClaudeBorder
val PrimaryText = Warm900
val DarkPrimaryText = ClaudeTextPrimary
val Slate50 = Warm50
val Slate100 = Warm100
val Slate200 = Warm200
val Slate300 = Warm300
val Slate400 = Warm400
val Slate500 = Warm500
val Slate600 = Warm600
val Slate700 = Warm700
val Slate800 = Warm800
val Slate900 = Warm900
val Slate950 = Gray950
val Zinc50 = Warm50
val Zinc100 = Warm100
val Zinc200 = Warm200
val Zinc300 = Warm300
val Zinc700 = Warm700
val Zinc800 = Warm800
val Zinc900 = Warm900
val Zinc950 = Gray950
val NeoCoralDark = SemanticError
val NeoAmberDark = SemanticWarning
val NeoAmberLight = NeoPastelYellow
val NeoSage = SemanticSuccess
val NeoSky = Color(0xFFBAE6FD)
val NeoTealDark = Color(0xFF14B8A6)
