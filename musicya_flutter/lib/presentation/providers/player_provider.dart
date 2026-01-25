import 'package:equatable/equatable.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:audio_service/audio_service.dart';
import '../../data/models/models.dart';
import 'service_providers.dart';

/// State of the audio player
class PlayerState extends Equatable {
  final Song? currentSong;
  final bool isPlaying;
  final Duration position;
  final Duration duration;
  final bool shuffleEnabled;
  final int repeatMode; // 0: off, 1: one, 2: all
  final List<Song> queue;
  final int currentIndex;
  final bool isLoading;

  const PlayerState({
    this.currentSong,
    this.isPlaying = false,
    this.position = Duration.zero,
    this.duration = Duration.zero,
    this.shuffleEnabled = false,
    this.repeatMode = 0,
    this.queue = const [],
    this.currentIndex = -1,
    this.isLoading = false,
  });

  PlayerState copyWith({
    Song? currentSong,
    bool? isPlaying,
    Duration? position,
    Duration? duration,
    bool? shuffleEnabled,
    int? repeatMode,
    List<Song>? queue,
    int? currentIndex,
    bool? isLoading,
  }) {
    return PlayerState(
      currentSong: currentSong ?? this.currentSong,
      isPlaying: isPlaying ?? this.isPlaying,
      position: position ?? this.position,
      duration: duration ?? this.duration,
      shuffleEnabled: shuffleEnabled ?? this.shuffleEnabled,
      repeatMode: repeatMode ?? this.repeatMode,
      queue: queue ?? this.queue,
      currentIndex: currentIndex ?? this.currentIndex,
      isLoading: isLoading ?? this.isLoading,
    );
  }

  bool get hasSong => currentSong != null;
  bool get isLastSong => currentIndex >= queue.length - 1;
  bool get isFirstSong => currentIndex <= 0;

  @override
  List<Object?> get props => [
        currentSong,
        isPlaying,
        position,
        duration,
        shuffleEnabled,
        repeatMode,
        queue,
        currentIndex,
        isLoading,
      ];
}

class PlayerNotifier extends Notifier<PlayerState> {
  @override
  PlayerState build() {
    // Listen to audio handler state changes
    final audioHandler = ref.read(audioHandlerProvider);
    
    // Subscribe to playback state
    audioHandler.playbackState.listen((playbackState) {
      final isPlaying = playbackState.playing;
      final processingState = playbackState.processingState;
      
      final isLoading = processingState == AudioProcessingState.loading ||
          processingState == AudioProcessingState.buffering;
          
      state = state.copyWith(
        isPlaying: isPlaying,
        isLoading: isLoading,
        position: playbackState.position,
        // Duration is handled by the durationStream listener below
      );
    });
    
    // Subscribe to current position stream for smooth UI updates
    audioHandler.positionStream.listen((position) {
      state = state.copyWith(position: position);
    });
    
    // Subscribe to duration changes
    audioHandler.durationStream.listen((duration) {
      state = state.copyWith(duration: duration);
    });

    return const PlayerState();
  }

  Future<void> setQueue(List<Song> newQueue, {int initialIndex = 0}) async {
    if (newQueue.isEmpty) return;
    
    state = state.copyWith(
      queue: newQueue,
      currentIndex: initialIndex,
      currentSong: newQueue[initialIndex],
      isLoading: true,
    );
    
    final audioHandler = ref.read(audioHandlerProvider);
    await audioHandler.playSong(newQueue[initialIndex]);
  }

  Future<void> togglePlayPause() async {
    final audioHandler = ref.read(audioHandlerProvider);
    if (state.isPlaying) {
      await audioHandler.pause();
    } else {
      await audioHandler.play();
    }
  }

  Future<void> seekTo(Duration position) async {
    final audioHandler = ref.read(audioHandlerProvider);
    await audioHandler.seek(position);
  }

  Future<void> skipToNext() async {
    if (state.queue.isEmpty) return;
    
    int nextIndex = state.currentIndex + 1;
    if (nextIndex >= state.queue.length) {
      if (state.repeatMode == 2) { // Repeat All
        nextIndex = 0;
      } else {
        return; // End of queue
      }
    }
    
    await _playAtIndex(nextIndex);
  }

  Future<void> skipToPrevious() async {
    if (state.queue.isEmpty) return;
    
    // If more than 3 seconds in, restart song
    if (state.position.inSeconds > 3) {
      await seekTo(Duration.zero);
      return;
    }
    
    int prevIndex = state.currentIndex - 1;
    if (prevIndex < 0) {
      if (state.repeatMode == 2) { // Repeat All
        prevIndex = state.queue.length - 1;
      } else {
        prevIndex = 0;
      }
    }
    
    await _playAtIndex(prevIndex);
  }
  
  Future<void> playSongAt(int index) async {
    if (index >= 0 && index < state.queue.length) {
      await _playAtIndex(index);
    }
  }

  Future<void> _playAtIndex(int index) async {
    final song = state.queue[index];
    state = state.copyWith(
      currentIndex: index,
      currentSong: song,
      isLoading: true,
    );
    
    final audioHandler = ref.read(audioHandlerProvider);
    await audioHandler.playSong(song);
  }

  void toggleShuffle() {
    state = state.copyWith(shuffleEnabled: !state.shuffleEnabled);
    // Logic for shuffling queue would go here
  }

  void toggleRepeat() {
    final nextMode = (state.repeatMode + 1) % 3;
    state = state.copyWith(repeatMode: nextMode);
  }
}

final playerProvider = NotifierProvider<PlayerNotifier, PlayerState>(() {
  return PlayerNotifier();
});
