package com.fourshil.musicya.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Clean Minimalistic Design System Dimensions
 * Consistent spacing, sizing, and corner radii for a polished UI
 */
object NeoDimens {

    // ═══════════════════════════════════════════════════════════════════════════
    // Elevation - Subtle Material shadows
    // ═══════════════════════════════════════════════════════════════════════════
    val ElevationNone = 0.dp
    val ElevationLow = 1.dp         // Subtle lift
    val ElevationMedium = 4.dp      // Cards, surfaces
    val ElevationHigh = 8.dp        // Modals, dialogs
    val ElevationHighest = 16.dp    // Floating elements

    // Legacy shadow aliases (for compatibility)
    val ShadowNone = ElevationNone
    val ShadowSmall = ElevationLow
    val ShadowMedium = ElevationMedium
    val ShadowLarge = ElevationHigh

    // ═══════════════════════════════════════════════════════════════════════════
    // Borders - Clean, thin lines
    // ═══════════════════════════════════════════════════════════════════════════
    val BorderNone = 0.dp
    val BorderThin = 1.dp           // Standard borders
    val BorderMedium = 1.5.dp       // Emphasis borders
    val BorderThick = 2.dp          // Rarely used

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
    // Corner Radius - Modern rounded corners
    // ═══════════════════════════════════════════════════════════════════════════
    val CornerNone = 0.dp
    val CornerXS = 4.dp             // Subtle rounding
    val CornerSmall = 8.dp          // Small elements
    val CornerMedium = 12.dp        // Cards, buttons
    val CornerLarge = 16.dp         // Large cards
    val CornerXL = 24.dp            // Sheets, dialogs
    val CornerFull = 999.dp         // Circular/pill shapes

    // ═══════════════════════════════════════════════════════════════════════════
    // Component Sizes
    // ═══════════════════════════════════════════════════════════════════════════
    val IconSmall = 16.dp
    val IconMedium = 24.dp
    val IconLarge = 32.dp
    val IconXL = 48.dp

    val ButtonHeightSmall = 36.dp
    val ButtonHeightMedium = 48.dp
    val ButtonHeightLarge = 56.dp

    val CardPadding = 16.dp
    val ScreenPadding = 16.dp       // Reduced from 24dp for cleaner look

    val AlbumArtSmall = 48.dp
    val AlbumArtMedium = 64.dp
    val AlbumArtLarge = 280.dp
    val AlbumArtXL = 320.dp

    val MiniPlayerHeight = 64.dp    // Slightly reduced
    val BottomNavHeight = 64.dp
    
    // Combined bottom padding for lists (Nav + MiniPlayer + spacing)
    val ListBottomPadding = 140.dp  // Slightly reduced
    
    // Top Header Space
    val HeaderHeight = 200.dp       // Reduced for cleaner look

    // ═══════════════════════════════════════════════════════════════════════════
    // Touch Targets
    // ═══════════════════════════════════════════════════════════════════════════
    val TouchTargetMin = 48.dp      // Minimum touch target size
    val TouchTargetMedium = 56.dp
    val TouchTargetLarge = 64.dp

    // ═══════════════════════════════════════════════════════════════════════════
    // Animation Durations (ms)
    // ═══════════════════════════════════════════════════════════════════════════
    const val AnimFast = 150
    const val AnimMedium = 250
    const val AnimSlow = 350
    const val AnimSpring = 500      // For spring animations
}
