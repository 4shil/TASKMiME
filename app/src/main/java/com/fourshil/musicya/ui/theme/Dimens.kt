package com.fourshil.musicya.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Soft Neo-Brutalism Design System Dimensions
 * Consistent spacing, sizing, and corner radii with soft, approachable feel
 */
object NeoDimens {

    // ═══════════════════════════════════════════════════════════════════════════
    // Soft Neo-Brutalism Shadows
    // Offset shadows but with softer appearance than hard brutalism
    // ═══════════════════════════════════════════════════════════════════════════
    val ShadowNone = 0.dp
    val ShadowSubtle = 2.dp         // List items, chips - subtle lift
    val ShadowDefault = 3.dp        // Cards, buttons - noticeable but soft
    val ShadowProminent = 4.dp      // Modals, featured elements
    val ShadowHero = 6.dp           // Hero elements, album art

    // Legacy elevation aliases
    val ElevationNone = ShadowNone
    val ElevationLow = ShadowSubtle
    val ElevationMedium = ShadowDefault
    val ElevationHigh = ShadowProminent
    val ElevationHighest = ShadowHero
    val ShadowSmall = ShadowSubtle
    val ShadowMedium = ShadowDefault
    val ShadowLarge = ShadowProminent

    // ═══════════════════════════════════════════════════════════════════════════
    // Borders - Consistent thickness hierarchy
    // ═══════════════════════════════════════════════════════════════════════════
    val BorderNone = 0.dp
    val BorderSubtle = 1.dp         // Dividers, subtle separation
    val BorderDefault = 1.5.dp      // Standard borders - cards, inputs
    val BorderBold = 2.dp           // Emphasis - buttons, selected states

    // Legacy aliases
    val BorderThin = BorderSubtle
    val BorderMedium = BorderDefault
    val BorderThick = BorderBold

    // ═══════════════════════════════════════════════════════════════════════════
    // Spacing - 4dp base unit system
    // ═══════════════════════════════════════════════════════════════════════════
    val SpacingNone = 0.dp
    val SpacingXXS = 2.dp           // Tight spacing
    val SpacingXS = 4.dp            // Compact elements
    val SpacingS = 8.dp             // Related elements
    val SpacingM = 12.dp            // Standard gap
    val SpacingL = 16.dp            // Section spacing
    val SpacingXL = 24.dp           // Major sections
    val SpacingXXL = 32.dp          // Screen-level spacing
    val SpacingHuge = 48.dp         // Hero sections

    // ═══════════════════════════════════════════════════════════════════════════
    // Corner Radius - Soft, friendly corners
    // ═══════════════════════════════════════════════════════════════════════════
    val CornerNone = 0.dp
    val CornerXS = 6.dp             // Subtle rounding
    val CornerSmall = 8.dp          // Thumbnails, small elements
    val CornerMedium = 12.dp        // Cards, buttons, inputs
    val CornerLarge = 16.dp         // Large cards, dialogs
    val CornerXL = 20.dp            // Sheets, modals
    val CornerHero = 24.dp          // Hero album art
    val CornerFull = 999.dp         // Pills, circular

    // ═══════════════════════════════════════════════════════════════════════════
    // Icon Sizes - Consistent scale
    // ═══════════════════════════════════════════════════════════════════════════
    val IconXS = 16.dp              // Inline indicators
    val IconSmall = 20.dp           // Secondary actions
    val IconMedium = 24.dp          // Standard icons
    val IconLarge = 28.dp           // Primary actions
    val IconXL = 32.dp              // Featured icons
    val IconXXL = 48.dp             // Hero icons
    val IconHero = 64.dp            // Empty states

    // ═══════════════════════════════════════════════════════════════════════════
    // Touch Targets - Accessibility compliant (min 48dp)
    // ═══════════════════════════════════════════════════════════════════════════
    val TouchTargetMin = 48.dp      // Minimum touch target - NEVER go below
    val TouchTargetMedium = 52.dp   // Comfortable touch
    val TouchTargetLarge = 56.dp    // Primary actions
    val TouchTargetHero = 64.dp     // Play button, main actions

    // ═══════════════════════════════════════════════════════════════════════════
    // Component Sizes
    // ═══════════════════════════════════════════════════════════════════════════
    val ButtonHeightSmall = 40.dp   // Compact buttons
    val ButtonHeightMedium = 48.dp  // Standard buttons
    val ButtonHeightLarge = 56.dp   // Primary actions

    val CardPadding = 16.dp
    val ScreenPadding = 20.dp       // Consistent screen margins

    val AlbumArtTiny = 40.dp        // Queue items
    val AlbumArtSmall = 52.dp       // List items
    val AlbumArtMedium = 64.dp      // Featured items
    val AlbumArtLarge = 280.dp      // Now playing
    val AlbumArtXL = 320.dp         // Fullscreen

    val MiniPlayerHeight = 68.dp    // Mini player bar
    val BottomNavHeight = 64.dp     // Bottom navigation
    
    // Combined bottom padding for lists
    val ListBottomPadding = 144.dp  // Nav + MiniPlayer + breathing room
    
    // Header
    val HeaderHeight = 180.dp

    // ═══════════════════════════════════════════════════════════════════════════
    // Animation Durations (ms)
    // ═══════════════════════════════════════════════════════════════════════════
    const val AnimFast = 150        // Micro-interactions
    const val AnimMedium = 250      // Standard transitions
    const val AnimSlow = 350        // Major transitions
    const val AnimSpring = 400      // Spring animations
}
