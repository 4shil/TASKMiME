package com.fourshil.musicya.ui.screens.library;

import androidx.lifecycle.SavedStateHandle;
import com.fourshil.musicya.data.repository.IMusicRepository;
import com.fourshil.musicya.player.PlayerController;
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
public final class ArtistDetailViewModel_Factory implements Factory<ArtistDetailViewModel> {
  private final Provider<IMusicRepository> repositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ArtistDetailViewModel_Factory(Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ArtistDetailViewModel get() {
    return newInstance(repositoryProvider.get(), playerControllerProvider.get(), savedStateHandleProvider.get());
  }

  public static ArtistDetailViewModel_Factory create(Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ArtistDetailViewModel_Factory(repositoryProvider, playerControllerProvider, savedStateHandleProvider);
  }

  public static ArtistDetailViewModel newInstance(IMusicRepository repository,
      PlayerController playerController, SavedStateHandle savedStateHandle) {
    return new ArtistDetailViewModel(repository, playerController, savedStateHandle);
  }
}
