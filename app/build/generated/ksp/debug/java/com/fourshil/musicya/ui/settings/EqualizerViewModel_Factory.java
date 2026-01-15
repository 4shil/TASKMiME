package com.fourshil.musicya.ui.settings;

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
public final class EqualizerViewModel_Factory implements Factory<EqualizerViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  public EqualizerViewModel_Factory(Provider<PlayerController> playerControllerProvider) {
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public EqualizerViewModel get() {
    return newInstance(playerControllerProvider.get());
  }

  public static EqualizerViewModel_Factory create(
      Provider<PlayerController> playerControllerProvider) {
    return new EqualizerViewModel_Factory(playerControllerProvider);
  }

  public static EqualizerViewModel newInstance(PlayerController playerController) {
    return new EqualizerViewModel(playerController);
  }
}
