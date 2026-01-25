import 'package:flutter/material.dart';
import 'package:musicya/presentation/theme/theme.dart';

/// Navigation item data class
class NavItem {
  final String route;
  final String label;
  final IconData icon;

  const NavItem({
    required this.route,
    required this.label,
    required this.icon,
  });
}

/// Clean Minimalistic Bottom Navigation
/// Material3 styled with pill-shaped indicator
class BottomNavigation extends StatelessWidget {
  final List<NavItem> items;
  final String? currentRoute;
  final ValueChanged<String> onItemTap;

  const BottomNavigation({
    super.key,
    required this.items,
    this.currentRoute,
    required this.onItemTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Material(
      elevation: AppDimens.elevationHigh,
      color: theme.colorScheme.surface,
      child: SizedBox(
        height: AppDimens.bottomNavHeight,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: items.map((item) {
            final selected = currentRoute == item.route;
            return _NavItemButton(
              item: item,
              selected: selected,
              onTap: () => onItemTap(item.route),
            );
          }).toList(),
        ),
      ),
    );
  }
}

class _NavItemButton extends StatelessWidget {
  final NavItem item;
  final bool selected;
  final VoidCallback onTap;

  const _NavItemButton({
    required this.item,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Icon with pill indicator
          AnimatedContainer(
            duration: AppDimens.animMedium,
            curve: Curves.easeOut,
            width: 56,
            height: 32,
            decoration: BoxDecoration(
              color: selected
                  ? theme.colorScheme.primaryContainer
                  : Colors.transparent,
              borderRadius: BorderRadius.circular(AppDimens.cornerFull),
            ),
            child: Icon(
              item.icon,
              size: AppDimens.iconMedium,
              color: selected
                  ? theme.colorScheme.onPrimaryContainer
                  : theme.colorScheme.onSurfaceVariant,
            ),
          ),
          const SizedBox(height: AppDimens.spacingXXS),
          // Label
          Text(
            item.label,
            style: theme.textTheme.labelSmall?.copyWith(
              color: selected
                  ? theme.colorScheme.onSurface
                  : theme.colorScheme.onSurfaceVariant,
            ),
          ),
        ],
      ),
    );
  }
}
