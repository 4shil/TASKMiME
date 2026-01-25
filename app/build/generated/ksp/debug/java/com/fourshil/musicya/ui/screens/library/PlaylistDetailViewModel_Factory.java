package com.fourshil.musicya.ui.screens.library;

import androidx.lifecycle.SavedStateHandle;
import com.fourshil.musicya.data.db.MusicDao;
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
public final class PlaylistDetailViewModel_Factory implements Factory<PlaylistDetailViewModel> {
  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<IMusicRepository> repositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public PlaylistDetailViewModel_Factory(Provider<MusicDao> musicDaoProvider,
      Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.musicDaoProvider = musicDaoProvider;
    this.repositoryProvider = repositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public PlaylistDetailViewModel get() {
    return newInstance(musicDaoProvider.get(), repositoryProvider.get(), playerControllerProvider.get(), savedStateHandleProvider.get());
  }

  public static PlaylistDetailViewModel_Factory create(Provider<MusicDao> musicDaoProvider,
      Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PlaylistDetailViewModel_Factory(musicDaoProvider, repositoryProvider, playerControllerProvider, savedStateHandleProvider);
  }

  public static PlaylistDetailViewModel newInstance(MusicDao musicDao, IMusicRepository repository,
      PlayerController playerController, SavedStateHandle savedStateHandle) {
    return new PlaylistDetailViewModel(musicDao, repository, playerController, savedStateHandle);
  }
}
