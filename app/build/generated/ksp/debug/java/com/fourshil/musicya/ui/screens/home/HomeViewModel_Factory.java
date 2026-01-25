package com.fourshil.musicya.ui.screens.home;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<IMusicRepository> repositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  public HomeViewModel_Factory(Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), playerControllerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<IMusicRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new HomeViewModel_Factory(repositoryProvider, playerControllerProvider);
  }

  public static HomeViewModel newInstance(IMusicRepository repository,
      PlayerController playerController) {
    return new HomeViewModel(repository, playerController);
  }
}
