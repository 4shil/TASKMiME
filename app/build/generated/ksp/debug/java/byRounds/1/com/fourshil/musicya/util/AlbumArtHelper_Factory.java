package com.fourshil.musicya.util;

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
public final class AlbumArtHelper_Factory implements Factory<AlbumArtHelper> {
  private final Provider<Context> contextProvider;

  public AlbumArtHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AlbumArtHelper get() {
    return newInstance(contextProvider.get());
  }

  public static AlbumArtHelper_Factory create(Provider<Context> contextProvider) {
    return new AlbumArtHelper_Factory(contextProvider);
  }

  public static AlbumArtHelper newInstance(Context context) {
    return new AlbumArtHelper(context);
  }
}
