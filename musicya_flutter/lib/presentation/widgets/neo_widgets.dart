import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class NeoCard extends StatelessWidget {
  final Widget child;
  final Color? color;
  final EdgeInsetsGeometry? padding;
  final VoidCallback? onTap;

  const NeoCard({
    super.key,
    required this.child,
    this.color,
    this.padding,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        boxShadow: const [
          BoxShadow(
            color: AppTheme.shadow,
            offset: Offset(AppTheme.shadowOffset, AppTheme.shadowOffset),
            blurRadius: 0,
          ),
        ],
        borderRadius: BorderRadius.circular(AppTheme.borderRadius),
      ),
      child: Material(
        color: color ?? AppTheme.surface,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppTheme.borderRadius),
          side: const BorderSide(color: AppTheme.border, width: AppTheme.borderWidth),
        ),
        clipBehavior: Clip.antiAlias,
        child: InkWell(
          onTap: onTap,
          child: Padding(
            padding: padding ?? const EdgeInsets.all(16.0),
            child: child,
          ),
        ),
      ),
    );
  }
}

class NeoButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final Color? color;
  final IconData? icon;

  const NeoButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.color,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        decoration: BoxDecoration(
          color: color ?? AppTheme.primary,
          borderRadius: BorderRadius.circular(AppTheme.borderRadius),
          border: Border.all(color: AppTheme.border, width: AppTheme.borderWidth),
          boxShadow: const [
            BoxShadow(
              color: AppTheme.shadow,
              offset: Offset(AppTheme.shadowOffset, AppTheme.shadowOffset),
              blurRadius: 0,
            ),
          ],
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (icon != null) ...[
              Icon(icon, color: AppTheme.border),
              const SizedBox(width: 8),
            ],
            Text(
              text,
              style: const TextStyle(
                color: AppTheme.border,
                fontWeight: FontWeight.w800,
                fontSize: 16,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
