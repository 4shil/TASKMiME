
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../services/audio_handler.dart';

/// Provider for the AudioHandler instance
/// Initialize this in main.dart via overrides if it needs to be async initialized
final audioHandlerProvider = Provider<MusicyaAudioHandler>((ref) {
  throw UnimplementedError('Provider was not initialized');
});
