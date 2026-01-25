import 'package:equatable/equatable.dart';

/// Playlist model representing a collection of songs
class Playlist extends Equatable {
  final int id;
  final String name;
  final String? description;
  final String? artworkUri;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int songCount;

  const Playlist({
    required this.id,
    required this.name,
    this.description,
    this.artworkUri,
    required this.createdAt,
    required this.updatedAt,
    this.songCount = 0,
  });

  /// Copy with updated fields
  Playlist copyWith({
    int? id,
    String? name,
    String? description,
    String? artworkUri,
    DateTime? createdAt,
    DateTime? updatedAt,
    int? songCount,
  }) {
    return Playlist(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      artworkUri: artworkUri ?? this.artworkUri,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      songCount: songCount ?? this.songCount,
    );
  }

  @override
  List<Object?> get props => [
        id,
        name,
        description,
        artworkUri,
        createdAt,
        updatedAt,
        songCount,
      ];
}
