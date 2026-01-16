package com.fourshil.musicya.data.repository;

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
public final class MusicRepository_Factory implements Factory<MusicRepository> {
  private final Provider<Context> contextProvider;

  public MusicRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MusicRepository get() {
    return newInstance(contextProvider.get());
  }

  public static MusicRepository_Factory create(Provider<Context> contextProvider) {
    return new MusicRepository_Factory(contextProvider);
  }

  public static MusicRepository newInstance(Context context) {
    return new MusicRepository(context);
  }
}
