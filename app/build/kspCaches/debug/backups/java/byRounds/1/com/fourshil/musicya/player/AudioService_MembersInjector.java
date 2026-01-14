package com.fourshil.musicya.player;

import androidx.media3.exoplayer.ExoPlayer;
import com.fourshil.musicya.audiofx.AudioEffectController;
import com.fourshil.musicya.data.repository.MusicRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AudioService_MembersInjector implements MembersInjector<AudioService> {
  private final Provider<ExoPlayer> playerProvider;

  private final Provider<AudioEffectController> audioEffectControllerProvider;

  private final Provider<MusicRepository> repositoryProvider;

  public AudioService_MembersInjector(Provider<ExoPlayer> playerProvider,
      Provider<AudioEffectController> audioEffectControllerProvider,
      Provider<MusicRepository> repositoryProvider) {
    this.playerProvider = playerProvider;
    this.audioEffectControllerProvider = audioEffectControllerProvider;
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<AudioService> create(Provider<ExoPlayer> playerProvider,
      Provider<AudioEffectController> audioEffectControllerProvider,
      Provider<MusicRepository> repositoryProvider) {
    return new AudioService_MembersInjector(playerProvider, audioEffectControllerProvider, repositoryProvider);
  }

  @Override
  public void injectMembers(AudioService instance) {
    injectPlayer(instance, playerProvider.get());
    injectAudioEffectController(instance, audioEffectControllerProvider.get());
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.AudioService.player")
  public static void injectPlayer(AudioService instance, ExoPlayer player) {
    instance.player = player;
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.AudioService.audioEffectController")
  public static void injectAudioEffectController(AudioService instance,
      AudioEffectController audioEffectController) {
    instance.audioEffectController = audioEffectController;
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.AudioService.repository")
  public static void injectRepository(AudioService instance, MusicRepository repository) {
    instance.repository = repository;
  }
}
