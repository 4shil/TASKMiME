package com.fourshil.musicya.ui.library;

import com.fourshil.musicya.data.repository.MusicRepository;
import com.fourshil.musicya.player.MusicServiceConnection;
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
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<MusicRepository> repositoryProvider;

  private final Provider<MusicServiceConnection> serviceConnectionProvider;

  public LibraryViewModel_Factory(Provider<MusicRepository> repositoryProvider,
      Provider<MusicServiceConnection> serviceConnectionProvider) {
    this.repositoryProvider = repositoryProvider;
    this.serviceConnectionProvider = serviceConnectionProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(repositoryProvider.get(), serviceConnectionProvider.get());
  }

  public static LibraryViewModel_Factory create(Provider<MusicRepository> repositoryProvider,
      Provider<MusicServiceConnection> serviceConnectionProvider) {
    return new LibraryViewModel_Factory(repositoryProvider, serviceConnectionProvider);
  }

  public static LibraryViewModel newInstance(MusicRepository repository,
      MusicServiceConnection serviceConnection) {
    return new LibraryViewModel(repository, serviceConnection);
  }
}
