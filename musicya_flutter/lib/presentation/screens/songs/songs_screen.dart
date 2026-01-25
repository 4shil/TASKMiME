import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:musicya/presentation/theme/theme.dart';
import 'package:musicya/presentation/widgets/widgets.dart';
import 'package:musicya/presentation/providers/providers.dart';

/// Songs Screen - displays list of all songs
class SongsScreen extends ConsumerWidget {
  const SongsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final songsAsync = ref.watch(songsProvider);
    final playerState = ref.watch(playerProvider);

    return songsAsync.when(
      loading: () => const Center(child: CircularProgressIndicator(color: AppTheme.primary)),
      error: (err, stack) => Center(child: Text('Error: $err')),
      data: (songs) {
        if (songs.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.music_off, size: 64, color: AppTheme.primary),
                const SizedBox(height: 16),
                Text(
                  'No songs found',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          );
        }

        return ListView.builder(
          padding: const EdgeInsets.only(
            left: 16, right: 16, top: 16,
            bottom: 100, // Space for mini player
          ),
          itemCount: songs.length,
          itemBuilder: (context, index) {
            final song = songs[index];
            final isPlaying = playerState.currentSong?.id == song.id;

            return Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: NeoCard(
                color: isPlaying ? AppTheme.primary : AppTheme.surface,
                padding: const EdgeInsets.all(12),
                onTap: () {
                  ref.read(playerProvider.notifier).setQueue(songs, initialIndex: index);
                },
                child: Row(
                  children: [
                    // Album Art Placeholder
                    Container(
                      width: 50, height: 50,
                      decoration: BoxDecoration(
                        color: isPlaying ? AppTheme.surface : AppTheme.background,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: AppTheme.border, width: 2),
                      ),
                      alignment: Alignment.center,
                      child: Icon(
                        Icons.music_note, 
                        color: isPlaying ? AppTheme.primary : AppTheme.text,
                      ),
                    ),
                    const SizedBox(width: 16),
                    // Song Info
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            song.title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 16,
                              color: AppTheme.text,
                            ),
                          ),
                          Text(
                            song.artist,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontWeight: FontWeight.w500,
                              fontSize: 14,
                              color: AppTheme.text.withOpacity(0.7),
                            ),
                          ),
                        ],
                      ),
                    ),
                    // More Options
                    IconButton(
                      icon: const Icon(Icons.more_vert),
                      onPressed: () {},
                      color: AppTheme.border,
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
