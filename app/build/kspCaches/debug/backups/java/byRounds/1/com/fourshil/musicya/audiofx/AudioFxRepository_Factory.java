package com.fourshil.musicya.audiofx;

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
public final class AudioFxRepository_Factory implements Factory<AudioFxRepository> {
  @Override
  public AudioFxRepository get() {
    return newInstance();
  }

  public static AudioFxRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioFxRepository newInstance() {
    return new AudioFxRepository();
  }

  private static final class InstanceHolder {
    private static final AudioFxRepository_Factory INSTANCE = new AudioFxRepository_Factory();
  }
}
