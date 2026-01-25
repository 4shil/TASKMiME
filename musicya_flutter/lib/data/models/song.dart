import 'package:equatable/equatable.dart';

/// Song model representing a music track
class Song extends Equatable {
  final int id;
  final String title;
  final String artist;
  final String album;
  final int duration; // in milliseconds
  final String? albumArtUri;
  final String path;
  final bool isFavorite;
  final int playCount;
  final DateTime? lastPlayedAt;

  const Song({
    required this.id,
    required this.title,
    required this.artist,
    required this.album,
    required this.duration,
    this.albumArtUri,
    required this.path,
    this.isFavorite = false,
    this.playCount = 0,
    this.lastPlayedAt,
  });

  /// Duration formatted as MM:SS
  String get durationFormatted {
    final totalSeconds = duration ~/ 1000;
    final minutes = totalSeconds ~/ 60;
    final seconds = totalSeconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }

  /// Copy with updated fields
  Song copyWith({
    int? id,
    String? title,
    String? artist,
    String? album,
    int? duration,
    String? albumArtUri,
    String? path,
    bool? isFavorite,
    int? playCount,
    DateTime? lastPlayedAt,
  }) {
    return Song(
      id: id ?? this.id,
      title: title ?? this.title,
      artist: artist ?? this.artist,
      album: album ?? this.album,
      duration: duration ?? this.duration,
      albumArtUri: albumArtUri ?? this.albumArtUri,
      path: path ?? this.path,
      isFavorite: isFavorite ?? this.isFavorite,
      playCount: playCount ?? this.playCount,
      lastPlayedAt: lastPlayedAt ?? this.lastPlayedAt,
    );
  }

  @override
  List<Object?> get props => [
        id,
        title,
        artist,
        album,
        duration,
        albumArtUri,
        path,
        isFavorite,
        playCount,
        lastPlayedAt,
      ];
}
