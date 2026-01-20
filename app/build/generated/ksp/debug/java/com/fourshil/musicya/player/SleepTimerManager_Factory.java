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
public final class SleepTimerManager_Factory implements Factory<SleepTimerManager> {
  @Override
  public SleepTimerManager get() {
    return newInstance();
  }

  public static SleepTimerManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SleepTimerManager newInstance() {
    return new SleepTimerManager();
  }

  private static final class InstanceHolder {
    private static final SleepTimerManager_Factory INSTANCE = new SleepTimerManager_Factory();
  }
}
