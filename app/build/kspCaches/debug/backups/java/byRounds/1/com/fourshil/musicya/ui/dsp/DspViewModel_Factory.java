package com.fourshil.musicya.ui.dsp;

import com.fourshil.musicya.audiofx.AudioFxRepository;
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
public final class DspViewModel_Factory implements Factory<DspViewModel> {
  private final Provider<AudioFxRepository> repositoryProvider;

  public DspViewModel_Factory(Provider<AudioFxRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DspViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DspViewModel_Factory create(Provider<AudioFxRepository> repositoryProvider) {
    return new DspViewModel_Factory(repositoryProvider);
  }

  public static DspViewModel newInstance(AudioFxRepository repository) {
    return new DspViewModel(repository);
  }
}
