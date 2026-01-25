import 'package:flutter/material.dart';

class AppTheme {
  // Soft Neo-Brutalism Palette
  static const Color primary = Color(0xFFA3E635); // Limewire Green
  static const Color secondary = Color(0xFFC084FC); // Soft Purple
  static const Color background = Color(0xFFF3F4F6); // Soft Gray
  static const Color surface = Colors.white;
  static const Color text = Color(0xFF1F2937); // Dark Gray
  static const Color border = Color(0xFF111827); // Almost Black
  static const Color shadow = Color(0xFF000000); // Pure Black

  static const double borderWidth = 2.0;
  static const double borderRadius = 12.0;
  static const double shadowOffset = 4.0;

  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      scaffoldBackgroundColor: background,
      primaryColor: primary,
      colorScheme: const ColorScheme.light(
        primary: primary,
        secondary: secondary,
        surface: surface,
        background: background,
        onBackground: text,
        onSurface: text,
      ),
      fontFamily: 'Roboto', // Or Inter if available
      cardTheme: CardTheme(
        color: surface,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(borderRadius),
          side: const BorderSide(color: border, width: borderWidth),
        ),
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: background,
        foregroundColor: text,
        elevation: 0,
        centerTitle: false,
        titleTextStyle: TextStyle(
          color: text,
          fontSize: 24,
          fontWeight: FontWeight.w900,
          letterSpacing: -0.5,
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primary,
          foregroundColor: border,
          elevation: 0,
          textStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(borderRadius),
            side: const BorderSide(color: border, width: borderWidth),
          ),
        ),
      ),
    );
  }
}
