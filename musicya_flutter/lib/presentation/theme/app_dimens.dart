/// Clean Minimalistic Design System Dimensions
/// Consistent spacing, sizing, and corner radii for a polished UI
class AppDimens {
  AppDimens._();

  // ═══════════════════════════════════════════════════════════════════════════
  // Elevation - Subtle Material shadows
  // ═══════════════════════════════════════════════════════════════════════════
  static const double elevationNone = 0;
  static const double elevationLow = 1;
  static const double elevationMedium = 4;
  static const double elevationHigh = 8;
  static const double elevationHighest = 16;

  // ═══════════════════════════════════════════════════════════════════════════
  // Borders - Clean, thin lines
  // ═══════════════════════════════════════════════════════════════════════════
  static const double borderNone = 0;
  static const double borderThin = 1;
  static const double borderMedium = 1.5;
  static const double borderThick = 2;

  // ═══════════════════════════════════════════════════════════════════════════
  // Spacing - Consistent padding and margins
  // ═══════════════════════════════════════════════════════════════════════════
  static const double spacingNone = 0;
  static const double spacingXXS = 2;
  static const double spacingXS = 4;
  static const double spacingS = 8;
  static const double spacingM = 12;
  static const double spacingL = 16;
  static const double spacingXL = 24;
  static const double spacingXXL = 32;
  static const double spacingHuge = 48;

  // ═══════════════════════════════════════════════════════════════════════════
  // Corner Radius - Modern rounded corners
  // ═══════════════════════════════════════════════════════════════════════════
  static const double cornerNone = 0;
  static const double cornerXS = 4;
  static const double cornerSmall = 8;
  static const double cornerMedium = 12;
  static const double cornerLarge = 16;
  static const double cornerXL = 24;
  static const double cornerFull = 999;

  // ═══════════════════════════════════════════════════════════════════════════
  // Component Sizes
  // ═══════════════════════════════════════════════════════════════════════════
  static const double iconSmall = 16;
  static const double iconMedium = 24;
  static const double iconLarge = 32;
  static const double iconXL = 48;

  static const double buttonHeightSmall = 36;
  static const double buttonHeightMedium = 48;
  static const double buttonHeightLarge = 56;

  static const double cardPadding = 16;
  static const double screenPadding = 16;

  static const double albumArtSmall = 48;
  static const double albumArtMedium = 64;
  static const double albumArtLarge = 280;
  static const double albumArtXL = 320;

  static const double miniPlayerHeight = 64;
  static const double bottomNavHeight = 64;

  // Combined bottom padding for lists (Nav + MiniPlayer + spacing)
  static const double listBottomPadding = 140;

  // Top Header Space
  static const double headerHeight = 200;

  // ═══════════════════════════════════════════════════════════════════════════
  // Touch Targets
  // ═══════════════════════════════════════════════════════════════════════════
  static const double touchTargetMin = 48;
  static const double touchTargetMedium = 56;
  static const double touchTargetLarge = 64;

  // ═══════════════════════════════════════════════════════════════════════════
  // Animation Durations
  // ═══════════════════════════════════════════════════════════════════════════
  static const Duration animFast = Duration(milliseconds: 150);
  static const Duration animMedium = Duration(milliseconds: 250);
  static const Duration animSlow = Duration(milliseconds: 350);
  static const Duration animSpring = Duration(milliseconds: 500);
}
