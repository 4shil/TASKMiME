import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:musicya/data/models/models.dart';

import 'package:musicya/presentation/widgets/widgets.dart';
import 'package:musicya/presentation/providers/providers.dart';

/// Favorites Screen - displays list of favorited songs
class FavoritesScreen extends ConsumerWidget {
  const FavoritesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final favoritesAsync = ref.watch(favoritesProvider);
    final playerState = ref.watch(playerProvider);
    final theme = Theme.of(context);

    return favoritesAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (err, stack) => Center(child: Text('Error: $err')),
      data: (favorites) {
        if (favorites.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.favorite_border,
                  size: 64,
                  color: theme.colorScheme.onSurfaceVariant,
                ),
                const SizedBox(height: AppDimens.spacingL),
                Text(
                  'No favorites yet',
                  style: theme.textTheme.titleMedium?.copyWith(
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
                const SizedBox(height: AppDimens.spacingS),
                Text(
                  'Tap the heart icon on any song to add it here',
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
              ],
            ),
          );
        }

        return ListView.builder(
          padding: const EdgeInsets.only(
            top: AppDimens.spacingL,
            bottom: AppDimens.listBottomPadding,
          ),
          itemCount: favorites.length,
          itemExtent: 72,
          findChildIndexCallback: (key) {
            final valueKey = key as ValueKey<int>;
            final index = favorites.indexWhere((s) => s.id == valueKey.value);
            return index >= 0 ? index : null;
          },
          itemBuilder: (context, index) {
            final song = favorites[index];
            final isPlaying = playerState.currentSong?.id == song.id;

            return Padding(
              key: ValueKey(song.id),
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.screenPadding,
                vertical: 2,
              ),
              child: SongListItem(
                song: song,
                isPlaying: isPlaying,
                isFavorite: true,
                onTap: () {
                   ref.read(playerProvider.notifier).setQueue(favorites, initialIndex: index);
                },
                onLongPress: () => debugPrint('Select favorite: ${song.title}'),
                onMoreTap: () => debugPrint('More options: ${song.title}'),
              ),
            );
          },
        );
      },
    );
  }
}
