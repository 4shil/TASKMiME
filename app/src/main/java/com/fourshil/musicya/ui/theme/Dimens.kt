package com.fourshil.musicya.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Neo-Brutalism Design System Dimensions
 * Centralized values for consistent spacing, shadows, and borders
 */
object NeoDimens {

    // ═══════════════════════════════════════════════════════════════════════════
    // Shadows - Small and clean for professional Neo-Brutalism
    // ═══════════════════════════════════════════════════════════════════════════
    val ShadowNone = 0.dp
    val ShadowSmall = 2.dp      // Subtle elevation
    val ShadowMedium = 3.dp     // Standard cards
    val ShadowLarge = 4.dp      // Elevated elements (modals, FABs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Borders - Clean Neo-Brutalism strokes
    // ═══════════════════════════════════════════════════════════════════════════
    val BorderThin = 2.dp       // Subtle borders
    val BorderMedium = 3.dp     // Standard component borders
    val BorderThick = 4.dp      // Emphasis borders (rarely used)

    // ═══════════════════════════════════════════════════════════════════════════
    // Spacing - Consistent padding and margins
    // ═══════════════════════════════════════════════════════════════════════════
    val SpacingNone = 0.dp
    val SpacingXXS = 2.dp
    val SpacingXS = 4.dp
    val SpacingS = 8.dp
    val SpacingM = 12.dp
    val SpacingL = 16.dp
    val SpacingXL = 24.dp
    val SpacingXXL = 32.dp
    val SpacingHuge = 48.dp

    // ═══════════════════════════════════════════════════════════════════════════
    // Component Sizes
    // ═══════════════════════════════════════════════════════════════════════════
    val IconSmall = 16.dp
    val IconMedium = 24.dp
    val IconLarge = 32.dp

    val ButtonHeightSmall = 36.dp
    val ButtonHeightMedium = 48.dp
    val ButtonHeightLarge = 56.dp

    val CardPadding = 12.dp
    val ScreenPadding = 24.dp

    val AlbumArtSmall = 48.dp
    val AlbumArtMedium = 64.dp
    val AlbumArtLarge = 280.dp

    val MiniPlayerHeight = 72.dp
    val BottomNavHeight = 64.dp
    
    // Combined bottom padding for lists (Nav + Play + Spacing)
    val ListBottomPadding = 160.dp 
    
    // Top Header Space (Title + Chips + Search)
    val HeaderHeight = 262.dp

    // ═══════════════════════════════════════════════════════════════════════════
    // Corner Radius - Subtle rounding (Neo-Brutalism is typically sharp)
    // ═══════════════════════════════════════════════════════════════════════════
    val CornerNone = 0.dp
    val CornerSmall = 2.dp
    val CornerMedium = 4.dp
    val CornerLarge = 8.dp

    // ═══════════════════════════════════════════════════════════════════════════
    // Animation Durations (for smooth 60fps)
    // ═══════════════════════════════════════════════════════════════════════════
    const val AnimFast = 150
    const val AnimMedium = 250
    const val AnimSlow = 350
}
