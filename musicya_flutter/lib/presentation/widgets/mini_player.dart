import 'package:flutter/material.dart';
import 'package:musicya/data/models/models.dart';
import 'package:musicya/presentation/theme/theme.dart';
import 'album_art_image.dart';

/// Clean Minimalistic Mini Player
/// Features swipe gestures for skip next/previous
class MiniPlayer extends StatefulWidget {
  final Song? song;
  final bool isPlaying;
  final double progress;
  final VoidCallback onPlayPause;
  final VoidCallback onNext;
  final VoidCallback onPrevious;
  final VoidCallback onTap;

  const MiniPlayer({
    super.key,
    this.song,
    required this.isPlaying,
    required this.progress,
    required this.onPlayPause,
    required this.onNext,
    required this.onPrevious,
    required this.onTap,
  });

  @override
  State<MiniPlayer> createState() => _MiniPlayerState();
}

class _MiniPlayerState extends State<MiniPlayer> {
  double _dragOffset = 0;
  static const _swipeThreshold = 60.0;

  @override
  Widget build(BuildContext context) {
    if (widget.song == null) return const SizedBox.shrink();

    final theme = Theme.of(context);

    return GestureDetector(
      onHorizontalDragUpdate: (details) {
        setState(() {
          _dragOffset = (_dragOffset + details.delta.dx)
              .clamp(-_swipeThreshold * 2, _swipeThreshold * 2);
        });
      },
      onHorizontalDragEnd: (details) {
        if (_dragOffset < -_swipeThreshold) {
          widget.onNext();
        } else if (_dragOffset > _swipeThreshold) {
          widget.onPrevious();
        }
        setState(() => _dragOffset = 0);
      },
      onTap: widget.onTap,
      child: Material(
        elevation: AppDimens.elevationHigh,
        borderRadius: const BorderRadius.vertical(
          top: Radius.circular(AppDimens.cornerLarge),
        ),
        color: theme.colorScheme.surface,
        child: SizedBox(
          height: AppDimens.miniPlayerHeight,
          child: Column(
            children: [
              // Progress Bar
              LinearProgressIndicator(
                value: widget.progress.clamp(0, 1),
                minHeight: 3,
                backgroundColor: theme.colorScheme.surfaceContainerHighest,
                valueColor:
                    AlwaysStoppedAnimation(theme.colorScheme.primary),
              ),
              // Content
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.spacingL,
                  ),
                  child: Row(
                    children: [
                      // Song Info with Animation
                      Expanded(
                        child: AnimatedSwitcher(
                          duration: AppDimens.animMedium,
                          transitionBuilder: (child, animation) {
                            return FadeTransition(
                              opacity: animation,
                              child: SlideTransition(
                                position: Tween(
                                  begin: const Offset(0.3, 0),
                                  end: Offset.zero,
                                ).animate(animation),
                                child: child,
                              ),
                            );
                          },
                          child: _SongInfo(
                            key: ValueKey(widget.song!.id),
                            song: widget.song!,
                          ),
                        ),
                      ),
                      const SizedBox(width: AppDimens.spacingS),
                      // Play/Pause Button
                      _PlayPauseButton(
                        isPlaying: widget.isPlaying,
                        onPressed: widget.onPlayPause,
                      ),
                      // Next Button
                      IconButton(
                        onPressed: widget.onNext,
                        icon: Icon(
                          Icons.skip_next,
                          color: theme.colorScheme.onSurfaceVariant,
                          size: AppDimens.iconMedium,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SongInfo extends StatelessWidget {
  final Song song;

  const _SongInfo({super.key, required this.song});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Row(
      children: [
        AlbumArtImage(
          uri: song.albumArtUri,
          size: AppDimens.albumArtSmall,
        ),
        const SizedBox(width: AppDimens.spacingM),
        Expanded(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                song.title,
                style: theme.textTheme.bodyLarge?.copyWith(
                  fontWeight: FontWeight.w500,
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
      ],
    );
  }
}

class _PlayPauseButton extends StatelessWidget {
  final bool isPlaying;
  final VoidCallback onPressed;

  const _PlayPauseButton({
    required this.isPlaying,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return IconButton(
      onPressed: onPressed,
      style: IconButton.styleFrom(
        backgroundColor: theme.colorScheme.primary,
        foregroundColor: theme.colorScheme.onPrimary,
      ),
      icon: Icon(
        isPlaying ? Icons.pause : Icons.play_arrow,
        size: AppDimens.iconMedium,
      ),
    );
  }
}
