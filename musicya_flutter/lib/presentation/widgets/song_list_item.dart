import 'package:flutter/material.dart';
import 'package:musicya/data/models/models.dart';
import 'package:musicya/presentation/theme/theme.dart';
import 'album_art_image.dart';

/// Clean Minimalistic Song List Item
/// Simple, readable design with proper touch targets
class SongListItem extends StatelessWidget {
  final Song song;
  final bool isPlaying;
  final bool isFavorite;
  final bool isSelected;
  final bool isSelectionMode;
  final VoidCallback onTap;
  final VoidCallback onLongPress;
  final VoidCallback onMoreTap;

  const SongListItem({
    super.key,
    required this.song,
    this.isPlaying = false,
    this.isFavorite = false,
    this.isSelected = false,
    this.isSelectionMode = false,
    required this.onTap,
    required this.onLongPress,
    required this.onMoreTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    final backgroundColor = isSelected
        ? theme.colorScheme.primaryContainer.withOpacity(0.3)
        : isPlaying
            ? theme.colorScheme.primaryContainer.withOpacity(0.15)
            : Colors.transparent;

    final contentColor = isPlaying
        ? theme.colorScheme.primary
        : theme.colorScheme.onSurface;

    return RepaintBoundary(
      child: Material(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(AppDimens.cornerMedium),
        child: InkWell(
          onTap: onTap,
          onLongPress: onLongPress,
          borderRadius: BorderRadius.circular(AppDimens.cornerMedium),
          child: Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.spacingL,
              vertical: AppDimens.spacingXS,
            ),
            child: Row(
              children: [
                // Album Art or Selection Checkbox
                if (isSelectionMode && isSelected)
                  Container(
                    width: AppDimens.albumArtSmall,
                    height: AppDimens.albumArtSmall,
                    decoration: BoxDecoration(
                      color: theme.colorScheme.primary,
                      borderRadius:
                          BorderRadius.circular(AppDimens.cornerSmall),
                    ),
                    child: Icon(
                      Icons.check,
                      color: theme.colorScheme.onPrimary,
                      size: AppDimens.iconMedium,
                    ),
                  )
                else
                  AlbumArtImage(
                    uri: song.albumArtUri,
                    size: AppDimens.albumArtSmall,
                  ),

                const SizedBox(width: AppDimens.spacingM),

                // Song Info
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        song.title,
                        style: theme.textTheme.bodyLarge?.copyWith(
                          fontWeight:
                              isPlaying ? FontWeight.w600 : FontWeight.normal,
                          color: contentColor,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      Text(
                        song.artist,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: theme.colorScheme.onSurfaceVariant,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                ),

                // Favorite Indicator
                if (isFavorite && !isSelectionMode) ...[
                  Icon(
                    Icons.favorite,
                    size: AppDimens.iconSmall,
                    color: theme.colorScheme.primary,
                  ),
                  const SizedBox(width: AppDimens.spacingXS),
                ],

                // Duration
                Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.spacingS,
                  ),
                  child: Text(
                    song.durationFormatted,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),

                // More Button
                if (!isSelectionMode)
                  IconButton(
                    onPressed: onMoreTap,
                    icon: Icon(
                      Icons.more_vert,
                      color: theme.colorScheme.onSurfaceVariant,
                      size: AppDimens.iconMedium,
                    ),
                    constraints: const BoxConstraints(
                      minWidth: AppDimens.touchTargetMin,
                      minHeight: AppDimens.touchTargetMin,
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
