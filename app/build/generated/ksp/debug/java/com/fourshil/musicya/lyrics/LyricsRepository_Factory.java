package com.fourshil.musicya.lyrics;

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
public final class LyricsRepository_Factory implements Factory<LyricsRepository> {
  @Override
  public LyricsRepository get() {
    return newInstance();
  }

  public static LyricsRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LyricsRepository newInstance() {
    return new LyricsRepository();
  }

  private static final class InstanceHolder {
    private static final LyricsRepository_Factory INSTANCE = new LyricsRepository_Factory();
  }
}
