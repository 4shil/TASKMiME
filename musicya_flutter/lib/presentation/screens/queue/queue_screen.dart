import 'package:flutter/material.dart';
import 'package:musicya/data/models/models.dart';
import 'package:musicya/presentation/theme/theme.dart';
import 'package:musicya/presentation/widgets/widgets.dart';

/// Queue Screen - displays current playback queue
class QueueScreen extends StatelessWidget {
  final List<Song> queue;
  final int currentIndex;
  final VoidCallback onBack;
  final ValueChanged<int> onSongTap;
  final ValueChanged<int> onRemove;

  const QueueScreen({
    super.key,
    required this.queue,
    this.currentIndex = 0,
    required this.onBack,
    required this.onSongTap,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: onBack,
        ),
        title: const Text('Queue'),
        actions: [
          TextButton(
            onPressed: () => debugPrint('Clear queue'),
            child: Text(
              'Clear',
              style: TextStyle(color: theme.colorScheme.primary),
            ),
          ),
        ],
      ),
      body: queue.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.queue_music,
                    size: 64,
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(height: AppDimens.spacingL),
                  Text(
                    'Queue is empty',
                    style: theme.textTheme.titleMedium?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                  ),
                ],
              ),
            )
          : ReorderableListView.builder(
              padding: const EdgeInsets.only(
                top: AppDimens.spacingL,
                bottom: AppDimens.listBottomPadding,
              ),
              itemCount: queue.length,
              onReorder: (oldIndex, newIndex) {
                debugPrint('Reorder: $oldIndex -> $newIndex');
              },
              itemBuilder: (context, index) {
                final song = queue[index];
                final isCurrentlyPlaying = index == currentIndex;

                return Padding(
                  key: ValueKey(song.id),
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.screenPadding,
                    vertical: 2,
                  ),
                  child: _QueueItem(
                    song: song,
                    isPlaying: isCurrentlyPlaying,
                    onTap: () => onSongTap(index),
                    onRemove: () => onRemove(index),
                  ),
                );
              },
            ),
    );
  }
}

class _QueueItem extends StatelessWidget {
  final Song song;
  final bool isPlaying;
  final VoidCallback onTap;
  final VoidCallback onRemove;

  const _QueueItem({
    required this.song,
    this.isPlaying = false,
    required this.onTap,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Material(
      color: isPlaying
          ? theme.colorScheme.primaryContainer.withOpacity(0.15)
          : Colors.transparent,
      borderRadius: BorderRadius.circular(AppDimens.cornerMedium),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(AppDimens.cornerMedium),
        child: Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: AppDimens.spacingM,
            vertical: AppDimens.spacingS,
          ),
          child: Row(
            children: [
              // Drag handle
              Icon(
                Icons.drag_handle,
                color: theme.colorScheme.onSurfaceVariant,
                size: AppDimens.iconMedium,
              ),
              const SizedBox(width: AppDimens.spacingS),
              // Album art
              AlbumArtImage(
                uri: song.albumArtUri,
                size: AppDimens.albumArtSmall,
              ),
              const SizedBox(width: AppDimens.spacingM),
              // Song info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (isPlaying)
                      Text(
                        'Now Playing',
                        style: theme.textTheme.labelSmall?.copyWith(
                          color: theme.colorScheme.primary,
                        ),
                      ),
                    Text(
                      song.title,
                      style: theme.textTheme.bodyLarge?.copyWith(
                        fontWeight: isPlaying ? FontWeight.w600 : null,
                        color: isPlaying
                            ? theme.colorScheme.primary
                            : theme.colorScheme.onSurface,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      song.artist,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ),
              // Remove button
              IconButton(
                onPressed: onRemove,
                icon: Icon(
                  Icons.close,
                  color: theme.colorScheme.onSurfaceVariant,
                  size: AppDimens.iconSmall,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
