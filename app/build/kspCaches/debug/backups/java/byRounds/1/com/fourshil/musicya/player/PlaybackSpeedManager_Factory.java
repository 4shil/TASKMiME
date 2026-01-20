package com.fourshil.musicya.player;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PlaybackSpeedManager_Factory implements Factory<PlaybackSpeedManager> {
  @Override
  public PlaybackSpeedManager get() {
    return newInstance();
  }

  public static PlaybackSpeedManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PlaybackSpeedManager newInstance() {
    return new PlaybackSpeedManager();
  }

  private static final class InstanceHolder {
    private static final PlaybackSpeedManager_Factory INSTANCE = new PlaybackSpeedManager_Factory();
  }
}
