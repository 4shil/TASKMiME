import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/song_repository.dart';
import '../../data/models/models.dart';

/// Provider for the SongRepository
final songRepositoryProvider = Provider<SongRepository>((ref) {
  return MockSongRepository();
});

/// Provider for the list of all songs
final songsProvider = FutureProvider<List<Song>>((ref) async {
  final repository = ref.watch(songRepositoryProvider);
  return repository.getSongs();
});

/// Provider for favorite songs
final favoritesProvider = FutureProvider<List<Song>>((ref) async {
  final repository = ref.watch(songRepositoryProvider);
  return repository.getFavorites();
});
