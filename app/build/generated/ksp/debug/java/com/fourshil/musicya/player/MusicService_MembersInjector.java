package com.fourshil.musicya.player;

import com.fourshil.musicya.data.SettingsPreferences;
import com.fourshil.musicya.data.db.MusicDao;
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

  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<CrossfadeManager> crossfadeManagerProvider;

  private final Provider<SettingsPreferences> settingsPreferencesProvider;

  public MusicService_MembersInjector(Provider<AudioEngine> audioEngineProvider,
      Provider<MusicDao> musicDaoProvider, Provider<CrossfadeManager> crossfadeManagerProvider,
      Provider<SettingsPreferences> settingsPreferencesProvider) {
    this.audioEngineProvider = audioEngineProvider;
    this.musicDaoProvider = musicDaoProvider;
    this.crossfadeManagerProvider = crossfadeManagerProvider;
    this.settingsPreferencesProvider = settingsPreferencesProvider;
  }

  public static MembersInjector<MusicService> create(Provider<AudioEngine> audioEngineProvider,
      Provider<MusicDao> musicDaoProvider, Provider<CrossfadeManager> crossfadeManagerProvider,
      Provider<SettingsPreferences> settingsPreferencesProvider) {
    return new MusicService_MembersInjector(audioEngineProvider, musicDaoProvider, crossfadeManagerProvider, settingsPreferencesProvider);
  }

  @Override
  public void injectMembers(MusicService instance) {
    injectAudioEngine(instance, audioEngineProvider.get());
    injectMusicDao(instance, musicDaoProvider.get());
    injectCrossfadeManager(instance, crossfadeManagerProvider.get());
    injectSettingsPreferences(instance, settingsPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.MusicService.audioEngine")
  public static void injectAudioEngine(MusicService instance, AudioEngine audioEngine) {
    instance.audioEngine = audioEngine;
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.MusicService.musicDao")
  public static void injectMusicDao(MusicService instance, MusicDao musicDao) {
    instance.musicDao = musicDao;
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.MusicService.crossfadeManager")
  public static void injectCrossfadeManager(MusicService instance,
      CrossfadeManager crossfadeManager) {
    instance.crossfadeManager = crossfadeManager;
  }

  @InjectedFieldSignature("com.fourshil.musicya.player.MusicService.settingsPreferences")
  public static void injectSettingsPreferences(MusicService instance,
      SettingsPreferences settingsPreferences) {
    instance.settingsPreferences = settingsPreferences;
  }
}
