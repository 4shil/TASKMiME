import 'package:audio_service/audio_service.dart';
import 'package:flutter/services.dart';

/// AudioHandler implementation that bridges to native Android MusicService
class MusicyaAudioHandler extends BaseAudioHandler with QueueHandler, SeekHandler {
  static const _channelAudio = MethodChannel('com.fourshil.musicya/audio');
  static const _channelEvents = EventChannel('com.fourshil.musicya/events');

  MusicyaAudioHandler() {
    _init();
  }

  void _init() {
    // Listen to native events
    _channelEvents.receiveBroadcastStream().listen((event) {
      if (event is Map) {
         final map = Map<String, dynamic>.from(event);
         _updateState(map);
      }
    }, onError: (e) {
      // ignore: avoid_print
      print("Error receiving audio events: $e");
    });
  }

  void _updateState(Map<String, dynamic> map) {
      final isPlaying = map['isPlaying'] as bool? ?? false;
      final position = map['position'] as int? ?? 0;
      final duration = map['duration'] as int? ?? 0;
      final title = map['title'] as String? ?? '';
      final artist = map['artist'] as String? ?? '';
      final album = map['album'] as String? ?? '';
      final uri = map['uri'] as String? ?? '';
      
      // Update playback state
      playbackState.add(playbackState.value.copyWith(
        controls: [
          MediaControl.skipToPrevious,
          if (isPlaying) MediaControl.pause else MediaControl.play,
          MediaControl.skipToNext,
        ],
        systemActions: const {
          MediaAction.seek,
          MediaAction.seekForward,
          MediaAction.seekBackward,
        },
        androidCompactActionIndices: const [0, 1, 2],
        playing: isPlaying,
        updatePosition: Duration(milliseconds: position),
        bufferedPosition: Duration(milliseconds: duration), 
        speed: 1.0,
        queueIndex: 0, // Simplified for now
      ));
      
      // Update metadata
      if (mediaItem.value?.id != uri || mediaItem.value?.title != title) {
        mediaItem.add(MediaItem(
           id: uri,
           title: title,
           artist: artist,
           album: album,
           duration: Duration(milliseconds: duration),
           artUri: null, // Need to handle art via utility or separate channel if needed
        ));
      }
  }

  @override
  Future<void> play() => _channelAudio.invokeMethod('play');

  @override
  Future<void> pause() => _channelAudio.invokeMethod('pause');

  @override
  Future<void> skipToNext() => _channelAudio.invokeMethod('skipToNext');

  @override
  Future<void> skipToPrevious() => _channelAudio.invokeMethod('skipToPrevious');
  
  @override
  Future<void> seek(Duration position) => _channelAudio.invokeMethod('seek', {'position': position.inMilliseconds});
}
