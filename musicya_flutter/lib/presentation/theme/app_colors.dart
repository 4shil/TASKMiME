import 'package:flutter/material.dart';

/// Clean Minimalistic Color Palette
/// Modern, neutral colors with subtle accent for a polished experience
class AppColors {
  AppColors._();

  // ─────────────────────────────────────────────────────────────────────────────
  // Base Colors
  // ─────────────────────────────────────────────────────────────────────────────
  static const pureBlack = Color(0xFF000000);
  static const pureWhite = Color(0xFFFFFFFF);

  // ─────────────────────────────────────────────────────────────────────────────
  // Primary Accent - Modern Blue
  // ─────────────────────────────────────────────────────────────────────────────
  static const accentPrimary = Color(0xFF2563EB);
  static const accentPrimaryDark = Color(0xFF60A5FA);
  static const accentSecondary = Color(0xFF10B981);
  static const accentError = Color(0xFFEF4444);
  static const accentWarning = Color(0xFFF59E0B);

  // ─────────────────────────────────────────────────────────────────────────────
  // Neutral Gray Scale - Clean and Modern
  // ─────────────────────────────────────────────────────────────────────────────
  static const gray50 = Color(0xFFFAFAFA);
  static const gray100 = Color(0xFFF5F5F5);
  static const gray200 = Color(0xFFE5E5E5);
  static const gray300 = Color(0xFFD4D4D4);
  static const gray400 = Color(0xFFA3A3A3);
  static const gray500 = Color(0xFF737373);
  static const gray600 = Color(0xFF525252);
  static const gray700 = Color(0xFF404040);
  static const gray800 = Color(0xFF262626);
  static const gray900 = Color(0xFF171717);
  static const gray950 = Color(0xFF0A0A0A);

  // ─────────────────────────────────────────────────────────────────────────────
  // Semantic Colors - Light Mode
  // ─────────────────────────────────────────────────────────────────────────────
  static const lightBackground = gray50;
  static const lightSurface = pureWhite;
  static const lightSurfaceVariant = gray100;
  static const lightOnBackground = gray900;
  static const lightOnSurface = gray900;
  static const lightOnSurfaceVariant = gray600;
  static const lightOutline = gray300;
  static const lightOutlineVariant = gray200;

  // ─────────────────────────────────────────────────────────────────────────────
  // Semantic Colors - Dark Mode
  // ─────────────────────────────────────────────────────────────────────────────
  static const darkBackground = gray950;
  static const darkSurface = gray900;
  static const darkSurfaceVariant = gray800;
  static const darkOnBackground = gray100;
  static const darkOnSurface = gray100;
  static const darkOnSurfaceVariant = gray400;
  static const darkOutline = gray700;
  static const darkOutlineVariant = gray800;

  // ─────────────────────────────────────────────────────────────────────────────
  // Progress/Player Accent
  // ─────────────────────────────────────────────────────────────────────────────
  static const progressColor = accentPrimary;
  static const progressColorDark = accentPrimaryDark;
}
