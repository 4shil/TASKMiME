import '../models/models.dart';

/// Abstract repository for fetching music data
abstract class SongRepository {
  Future<List<Song>> getSongs();
  Future<List<Song>> getFavorites();
  Future<void> toggleFavorite(int songId);
}

/// Mock implementation for initial development
class MockSongRepository implements SongRepository {
  final List<Song> _mockSongs = List.generate(
    20,
    (index) => Song(
      id: index,
      title: 'Song Title ${index + 1}',
      artist: 'Artist ${(index % 5) + 1}',
      album: 'Album ${(index % 3) + 1}',
      duration: (180 + index * 15) * 1000,
      path: '/storage/music/song_$index.mp3',
      isFavorite: index % 4 == 0,
    ),
  );

  @override
  Future<List<Song>> getSongs() async {
    // Simulate network/db delay
    await Future.delayed(const Duration(milliseconds: 800));
    return _mockSongs;
  }

  @override
  Future<List<Song>> getFavorites() async {
    await Future.delayed(const Duration(milliseconds: 400));
    return _mockSongs.where((s) => s.isFavorite).toList();
  }

  @override
  Future<void> toggleFavorite(int songId) async {
    // In a real app, this would update the DB
    final index = _mockSongs.indexWhere((s) => s.id == songId);
    if (index != -1) {
      final song = _mockSongs[index];
      _mockSongs[index] = song.copyWith(isFavorite: !song.isFavorite);
    }
  }
}
