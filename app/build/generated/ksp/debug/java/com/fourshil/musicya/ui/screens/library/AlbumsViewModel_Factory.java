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
public final class AlbumsViewModel_Factory implements Factory<AlbumsViewModel> {
  private final Provider<IMusicRepository> repositoryProvider;

  public AlbumsViewModel_Factory(Provider<IMusicRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AlbumsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static AlbumsViewModel_Factory create(Provider<IMusicRepository> repositoryProvider) {
    return new AlbumsViewModel_Factory(repositoryProvider);
  }

  public static AlbumsViewModel newInstance(IMusicRepository repository) {
    return new AlbumsViewModel(repository);
  }
}
