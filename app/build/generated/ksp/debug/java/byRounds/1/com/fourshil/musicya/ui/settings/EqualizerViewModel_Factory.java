package com.fourshil.musicya.ui.settings;

import com.fourshil.musicya.player.AudioEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class EqualizerViewModel_Factory implements Factory<EqualizerViewModel> {
  private final Provider<AudioEngine> audioEngineProvider;

  public EqualizerViewModel_Factory(Provider<AudioEngine> audioEngineProvider) {
    this.audioEngineProvider = audioEngineProvider;
  }

  @Override
  public EqualizerViewModel get() {
    return newInstance(audioEngineProvider.get());
  }

  public static EqualizerViewModel_Factory create(Provider<AudioEngine> audioEngineProvider) {
    return new EqualizerViewModel_Factory(audioEngineProvider);
  }

  public static EqualizerViewModel newInstance(AudioEngine audioEngine) {
    return new EqualizerViewModel(audioEngine);
  }
}
