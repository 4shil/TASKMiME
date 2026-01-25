import 'package:flutter/material.dart';
import 'package:musicya/presentation/theme/theme.dart';

/// Settings Screen - app configuration
class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ListView(
      padding: const EdgeInsets.all(AppDimens.screenPadding),
      children: [
        // Playback Section
        _SectionHeader(title: 'Playback'),
        _SettingsCard(children: [
          _SwitchTile(
            icon: Icons.graphic_eq,
            title: 'Gapless Playback',
            subtitle: 'Seamless transition between tracks',
            value: true,
            onChanged: (v) => debugPrint('Gapless: $v'),
          ),
          const Divider(height: 1),
          _SliderTile(
            icon: Icons.shuffle,
            title: 'Crossfade Duration',
            subtitle: '3 seconds',
            value: 3,
            min: 0,
            max: 12,
            onChanged: (v) => debugPrint('Crossfade: $v'),
          ),
          const Divider(height: 1),
          _NavigationTile(
            icon: Icons.equalizer,
            title: 'Equalizer',
            onTap: () => debugPrint('Open Equalizer'),
          ),
        ]),

        const SizedBox(height: AppDimens.spacingXL),

        // Library Section
        _SectionHeader(title: 'Library'),
        _SettingsCard(children: [
          _NavigationTile(
            icon: Icons.folder,
            title: 'Music Folders',
            subtitle: '2 folders selected',
            onTap: () => debugPrint('Open folders'),
          ),
          const Divider(height: 1),
          _SwitchTile(
            icon: Icons.visibility_off,
            title: 'Show Hidden Files',
            value: false,
            onChanged: (v) => debugPrint('Hidden: $v'),
          ),
        ]),

        const SizedBox(height: AppDimens.spacingXL),

        // Appearance Section
        _SectionHeader(title: 'Appearance'),
        _SettingsCard(children: [
          _NavigationTile(
            icon: Icons.palette,
            title: 'Theme',
            subtitle: 'System default',
            onTap: () => debugPrint('Open theme'),
          ),
        ]),

        const SizedBox(height: AppDimens.spacingXL),

        // About Section
        _SectionHeader(title: 'About'),
        _SettingsCard(children: [
          _NavigationTile(
            icon: Icons.info_outline,
            title: 'Version',
            subtitle: '1.0.0',
            onTap: () {},
          ),
        ]),

        const SizedBox(height: AppDimens.spacingXXL),
      ],
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String title;

  const _SectionHeader({required this.title});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(
        left: AppDimens.spacingS,
        bottom: AppDimens.spacingS,
      ),
      child: Text(
        title,
        style: theme.textTheme.labelLarge?.copyWith(
          color: theme.colorScheme.primary,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}

class _SettingsCard extends StatelessWidget {
  final List<Widget> children;

  const _SettingsCard({required this.children});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Material(
      color: theme.colorScheme.surface,
      borderRadius: BorderRadius.circular(AppDimens.cornerMedium),
      elevation: 0,
      child: Column(
        children: children,
      ),
    );
  }
}

class _SwitchTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final bool value;
  final ValueChanged<bool> onChanged;

  const _SwitchTile({
    required this.icon,
    required this.title,
    this.subtitle,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return ListTile(
      leading: Icon(icon, color: theme.colorScheme.onSurfaceVariant),
      title: Text(title),
      subtitle: subtitle != null ? Text(subtitle!) : null,
      trailing: Switch(
        value: value,
        onChanged: onChanged,
      ),
    );
  }
}

class _SliderTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final double value;
  final double min;
  final double max;
  final ValueChanged<double> onChanged;

  const _SliderTile({
    required this.icon,
    required this.title,
    this.subtitle,
    required this.value,
    required this.min,
    required this.max,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.spacingL,
        vertical: AppDimens.spacingS,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: theme.colorScheme.onSurfaceVariant),
              const SizedBox(width: AppDimens.spacingL),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: theme.textTheme.bodyLarge),
                    if (subtitle != null)
                      Text(
                        subtitle!,
                        style: theme.textTheme.bodySmall?.copyWith(
                          color: theme.colorScheme.onSurfaceVariant,
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ),
          Slider(
            value: value,
            min: min,
            max: max,
            divisions: (max - min).toInt(),
            onChanged: onChanged,
          ),
        ],
      ),
    );
  }
}

class _NavigationTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final VoidCallback onTap;

  const _NavigationTile({
    required this.icon,
    required this.title,
    this.subtitle,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return ListTile(
      leading: Icon(icon, color: theme.colorScheme.onSurfaceVariant),
      title: Text(title),
      subtitle: subtitle != null ? Text(subtitle!) : null,
      trailing: Icon(
        Icons.chevron_right,
        color: theme.colorScheme.onSurfaceVariant,
      ),
      onTap: onTap,
    );
  }
}
