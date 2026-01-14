package com.fourshil.musicya.audiofx;

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
public final class AudioEffectController_Factory implements Factory<AudioEffectController> {
  private final Provider<AudioFxRepository> repositoryProvider;

  public AudioEffectController_Factory(Provider<AudioFxRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AudioEffectController get() {
    return newInstance(repositoryProvider.get());
  }

  public static AudioEffectController_Factory create(
      Provider<AudioFxRepository> repositoryProvider) {
    return new AudioEffectController_Factory(repositoryProvider);
  }

  public static AudioEffectController newInstance(AudioFxRepository repository) {
    return new AudioEffectController(repository);
  }
}
