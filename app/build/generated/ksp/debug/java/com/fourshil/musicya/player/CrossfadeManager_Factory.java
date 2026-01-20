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
public final class CrossfadeManager_Factory implements Factory<CrossfadeManager> {
  @Override
  public CrossfadeManager get() {
    return newInstance();
  }

  public static CrossfadeManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CrossfadeManager newInstance() {
    return new CrossfadeManager();
  }

  private static final class InstanceHolder {
    private static final CrossfadeManager_Factory INSTANCE = new CrossfadeManager_Factory();
  }
}
