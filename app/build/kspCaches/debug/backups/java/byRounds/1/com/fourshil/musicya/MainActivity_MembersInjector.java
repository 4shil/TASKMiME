package com.fourshil.musicya;

import com.fourshil.musicya.player.PlayerController;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<PlayerController> playerControllerProvider;

  public MainActivity_MembersInjector(Provider<PlayerController> playerControllerProvider) {
    this.playerControllerProvider = playerControllerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<PlayerController> playerControllerProvider) {
    return new MainActivity_MembersInjector(playerControllerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPlayerController(instance, playerControllerProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.MainActivity.playerController")
  public static void injectPlayerController(MainActivity instance,
      PlayerController playerController) {
    instance.playerController = playerController;
  }
}
