package com.fourshil.musicya.player;

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
public final class MusicService_MembersInjector implements MembersInjector<MusicService> {
  private final Provider<AudioEngine> audioEngineProvider;

  public MusicService_MembersInjector(Provider<AudioEngine> audioEngineProvider) {
    this.audioEngineProvider = audioEngineProvider;
  }

  public static MembersInjector<MusicService> create(Provider<AudioEngine> audioEngineProvider) {
    return new MusicService_MembersInjector(audioEngineProvider);
  }

  @Override
  public void injectMembers(MusicService instance) {
    injectAudioEngine(instance, audioEngineProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.MusicService.audioEngine")
  public static void injectAudioEngine(MusicService instance, AudioEngine audioEngine) {
    instance.audioEngine = audioEngine;
  }
}
