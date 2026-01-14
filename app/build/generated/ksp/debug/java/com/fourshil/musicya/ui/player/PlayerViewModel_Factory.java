package com.fourshil.musicya.ui.player;

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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<MusicServiceConnection> serviceConnectionProvider;

  public PlayerViewModel_Factory(Provider<MusicServiceConnection> serviceConnectionProvider) {
    this.serviceConnectionProvider = serviceConnectionProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(serviceConnectionProvider.get());
  }

  public static PlayerViewModel_Factory create(
      Provider<MusicServiceConnection> serviceConnectionProvider) {
    return new PlayerViewModel_Factory(serviceConnectionProvider);
  }

  public static PlayerViewModel newInstance(MusicServiceConnection serviceConnection) {
    return new PlayerViewModel(serviceConnection);
  }
}
