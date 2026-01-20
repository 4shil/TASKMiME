package com.fourshil.musicya.player;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PlayerController_Factory implements Factory<PlayerController> {
  private final Provider<Context> contextProvider;

  private final Provider<SleepTimerManager> sleepTimerManagerProvider;

  private final Provider<PlaybackSpeedManager> speedManagerProvider;

  public PlayerController_Factory(Provider<Context> contextProvider,
      Provider<SleepTimerManager> sleepTimerManagerProvider,
      Provider<PlaybackSpeedManager> speedManagerProvider) {
    this.contextProvider = contextProvider;
    this.sleepTimerManagerProvider = sleepTimerManagerProvider;
    this.speedManagerProvider = speedManagerProvider;
  }

  @Override
  public PlayerController get() {
    return newInstance(contextProvider.get(), sleepTimerManagerProvider.get(), speedManagerProvider.get());
  }

  public static PlayerController_Factory create(Provider<Context> contextProvider,
      Provider<SleepTimerManager> sleepTimerManagerProvider,
      Provider<PlaybackSpeedManager> speedManagerProvider) {
    return new PlayerController_Factory(contextProvider, sleepTimerManagerProvider, speedManagerProvider);
  }

  public static PlayerController newInstance(Context context, SleepTimerManager sleepTimerManager,
      PlaybackSpeedManager speedManager) {
    return new PlayerController(context, sleepTimerManager, speedManager);
  }
}
