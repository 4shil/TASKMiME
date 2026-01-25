package com.fourshil.musicya.ui.screens.player;

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
public final class NowPlayingViewModel_Factory implements Factory<NowPlayingViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  public NowPlayingViewModel_Factory(Provider<PlayerController> playerControllerProvider) {
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public NowPlayingViewModel get() {
    return newInstance(playerControllerProvider.get());
  }

  public static NowPlayingViewModel_Factory create(
      Provider<PlayerController> playerControllerProvider) {
    return new NowPlayingViewModel_Factory(playerControllerProvider);
  }

  public static NowPlayingViewModel newInstance(PlayerController playerController) {
    return new NowPlayingViewModel(playerController);
  }
}
