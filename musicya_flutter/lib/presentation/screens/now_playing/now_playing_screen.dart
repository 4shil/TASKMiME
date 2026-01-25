import 'dart:math';
import 'package:flutter/material.dart';
import 'package:musicya/data/models/models.dart';
import 'package:musicya/presentation/theme/theme.dart';
import 'package:musicya/presentation/widgets/widgets.dart';

/// Now Playing Screen - full screen player with controls
class NowPlayingScreen extends StatefulWidget {
  final Song? song;
  final bool isPlaying;
  final Duration position;
  final Duration duration;
  final bool shuffleEnabled;
  final int repeatMode; // 0: off, 1: one, 2: all
  final bool isFavorite;
  final VoidCallback onBack;
  final VoidCallback onPlayPause;
  final VoidCallback onNext;
  final VoidCallback onPrevious;
  final VoidCallback onShuffle;
  final VoidCallback onRepeat;
  final VoidCallback onFavorite;
  final VoidCallback onQueue;
  final ValueChanged<Duration> onSeek;

  const NowPlayingScreen({
    super.key,
    this.song,
    required this.isPlaying,
    required this.position,
    required this.duration,
    this.shuffleEnabled = false,
    this.repeatMode = 0,
    this.isFavorite = false,
    required this.onBack,
    required this.onPlayPause,
    required this.onNext,
    required this.onPrevious,
    required this.onShuffle,
    required this.onRepeat,
    required this.onFavorite,
    required this.onQueue,
    required this.onSeek,
  });

  @override
  State<NowPlayingScreen> createState() => _NowPlayingScreenState();
}

class _NowPlayingScreenState extends State<NowPlayingScreen> {
  double _dragOffset = 0;
  static const _swipeThreshold = 100.0;

  String _formatDuration(Duration duration) {
    final minutes = duration.inMinutes;
    final seconds = duration.inSeconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final song = widget.song;

    if (song == null) {
      return Scaffold(
        body: Center(
          child: Text(
            'No song playing',
            style: theme.textTheme.titleMedium,
          ),
        ),
      );
    }

    final progress = widget.duration.inMilliseconds > 0
        ? widget.position.inMilliseconds / widget.duration.inMilliseconds
        : 0.0;

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            // Header
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.screenPadding,
                vertical: AppDimens.spacingL,
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  IconButton(
                    onPressed: widget.onBack,
                    icon: const Icon(Icons.arrow_back),
                  ),
                  Text(
                    'Now Playing',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  IconButton(
                    onPressed: widget.onQueue,
                    icon: const Icon(Icons.queue_music),
                  ),
                ],
              ),
            ),

            const Spacer(flex: 1),

            // Album Art with Swipe Gesture
            GestureDetector(
              onHorizontalDragUpdate: (details) {
                setState(() {
                  _dragOffset = (_dragOffset + details.delta.dx)
                      .clamp(-_swipeThreshold * 1.5, _swipeThreshold * 1.5);
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
              child: AnimatedContainer(
                duration: AppDimens.animMedium,
                transform: Matrix4.identity()
                  ..translate(_dragOffset)
                  ..rotateZ(_dragOffset / 50 * pi / 180),
                child: Container(
                  width: AppDimens.albumArtXL,
                  height: AppDimens.albumArtXL,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(AppDimens.cornerLarge),
                    boxShadow: [
                      BoxShadow(
                        color: theme.colorScheme.shadow.withOpacity(0.2),
                        blurRadius: 24,
                        offset: const Offset(0, 12),
                      ),
                    ],
                  ),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(AppDimens.cornerLarge),
                    child: AlbumArtImage(
                      uri: song.albumArtUri,
                      size: AppDimens.albumArtXL,
                      borderRadius: AppDimens.cornerLarge,
                    ),
                  ),
                ),
              ),
            ),

            const SizedBox(height: AppDimens.spacingXXL),

            // Song Info
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.screenPadding,
              ),
              child: Column(
                children: [
                  Text(
                    song.title,
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                    textAlign: TextAlign.center,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: AppDimens.spacingS),
                  Text(
                    song.artist,
                    style: theme.textTheme.bodyLarge?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            ),

            const SizedBox(height: AppDimens.spacingXXL),

            // Progress Slider
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.screenPadding,
              ),
              child: Column(
                children: [
                  SliderTheme(
                    data: SliderTheme.of(context).copyWith(
                      trackHeight: 4,
                      thumbShape: const RoundSliderThumbShape(
                        enabledThumbRadius: 6,
                      ),
                    ),
                    child: Slider(
                      value: progress.clamp(0.0, 1.0),
                      onChanged: (value) {
                        final newPosition = Duration(
                          milliseconds:
                              (value * widget.duration.inMilliseconds).toInt(),
                        );
                        widget.onSeek(newPosition);
                      },
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: AppDimens.spacingS,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          _formatDuration(widget.position),
                          style: theme.textTheme.labelSmall?.copyWith(
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                        Text(
                          _formatDuration(widget.duration),
                          style: theme.textTheme.labelSmall?.copyWith(
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),

            const Spacer(flex: 1),

            // Playback Controls
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.screenPadding,
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // Shuffle
                  IconButton(
                    onPressed: widget.onShuffle,
                    icon: Icon(
                      Icons.shuffle,
                      color: widget.shuffleEnabled
                          ? theme.colorScheme.primary
                          : theme.colorScheme.onSurfaceVariant,
                    ),
                    iconSize: 24,
                  ),
                  // Previous
                  IconButton(
                    onPressed: widget.onPrevious,
                    icon: const Icon(Icons.skip_previous),
                    iconSize: 32,
                  ),
                  // Play/Pause
                  Container(
                    width: 72,
                    height: 72,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: theme.colorScheme.primary,
                    ),
                    child: IconButton(
                      onPressed: widget.onPlayPause,
                      icon: Icon(
                        widget.isPlaying ? Icons.pause : Icons.play_arrow,
                        color: theme.colorScheme.onPrimary,
                      ),
                      iconSize: 36,
                    ),
                  ),
                  // Next
                  IconButton(
                    onPressed: widget.onNext,
                    icon: const Icon(Icons.skip_next),
                    iconSize: 32,
                  ),
                  // Repeat
                  IconButton(
                    onPressed: widget.onRepeat,
                    icon: Icon(
                      widget.repeatMode == 1 ? Icons.repeat_one : Icons.repeat,
                      color: widget.repeatMode != 0
                          ? theme.colorScheme.primary
                          : theme.colorScheme.onSurfaceVariant,
                    ),
                    iconSize: 24,
                  ),
                ],
              ),
            ),

            const SizedBox(height: AppDimens.spacingXL),

            // Secondary Actions
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  onPressed: widget.onFavorite,
                  icon: Icon(
                    widget.isFavorite ? Icons.favorite : Icons.favorite_border,
                    color: widget.isFavorite
                        ? AppColors.accentError
                        : theme.colorScheme.onSurfaceVariant,
                  ),
                ),
                const SizedBox(width: AppDimens.spacingL),
                IconButton(
                  onPressed: () => debugPrint('Lyrics'),
                  icon: Icon(
                    Icons.lyrics,
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
              ],
            ),

            const SizedBox(height: AppDimens.spacingXL),
          ],
        ),
      ),
    );
  }
}
