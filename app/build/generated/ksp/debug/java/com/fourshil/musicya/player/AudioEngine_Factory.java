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
public final class AudioEngine_Factory implements Factory<AudioEngine> {
  @Override
  public AudioEngine get() {
    return newInstance();
  }

  public static AudioEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioEngine newInstance() {
    return new AudioEngine();
  }

  private static final class InstanceHolder {
    private static final AudioEngine_Factory INSTANCE = new AudioEngine_Factory();
  }
}
