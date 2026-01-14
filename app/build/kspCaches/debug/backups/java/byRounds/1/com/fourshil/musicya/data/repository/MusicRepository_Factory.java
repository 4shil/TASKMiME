package com.fourshil.musicya.data.repository;

import android.content.Context;
import com.fourshil.musicya.data.local.MusicDao;
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

  private final Provider<MusicDao> musicDaoProvider;

  public MusicRepository_Factory(Provider<Context> contextProvider,
      Provider<MusicDao> musicDaoProvider) {
    this.contextProvider = contextProvider;
    this.musicDaoProvider = musicDaoProvider;
  }

  @Override
  public MusicRepository get() {
    return newInstance(contextProvider.get(), musicDaoProvider.get());
  }

  public static MusicRepository_Factory create(Provider<Context> contextProvider,
      Provider<MusicDao> musicDaoProvider) {
    return new MusicRepository_Factory(contextProvider, musicDaoProvider);
  }

  public static MusicRepository newInstance(Context context, MusicDao musicDao) {
    return new MusicRepository(context, musicDao);
  }
}
