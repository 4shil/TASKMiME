package com.fourshil.musicya.ui.screens.library;

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
public final class FavoritesViewModel_Factory implements Factory<FavoritesViewModel> {
  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<IMusicRepository> repositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  public FavoritesViewModel_Factory(Provider<MusicDao> musicDaoProvider,
      Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.musicDaoProvider = musicDaoProvider;
    this.repositoryProvider = repositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public FavoritesViewModel get() {
    return newInstance(musicDaoProvider.get(), repositoryProvider.get(), playerControllerProvider.get());
  }

  public static FavoritesViewModel_Factory create(Provider<MusicDao> musicDaoProvider,
      Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new FavoritesViewModel_Factory(musicDaoProvider, repositoryProvider, playerControllerProvider);
  }

  public static FavoritesViewModel newInstance(MusicDao musicDao, IMusicRepository repository,
      PlayerController playerController) {
    return new FavoritesViewModel(musicDao, repository, playerController);
  }
}
