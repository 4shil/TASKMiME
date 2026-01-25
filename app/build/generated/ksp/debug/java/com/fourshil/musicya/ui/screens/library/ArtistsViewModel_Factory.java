package com.fourshil.musicya.ui.screens.library;

import com.fourshil.musicya.data.repository.IMusicRepository;
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
public final class ArtistsViewModel_Factory implements Factory<ArtistsViewModel> {
  private final Provider<IMusicRepository> repositoryProvider;

  public ArtistsViewModel_Factory(Provider<IMusicRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ArtistsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ArtistsViewModel_Factory create(Provider<IMusicRepository> repositoryProvider) {
    return new ArtistsViewModel_Factory(repositoryProvider);
  }

  public static ArtistsViewModel newInstance(IMusicRepository repository) {
    return new ArtistsViewModel(repository);
  }
}
