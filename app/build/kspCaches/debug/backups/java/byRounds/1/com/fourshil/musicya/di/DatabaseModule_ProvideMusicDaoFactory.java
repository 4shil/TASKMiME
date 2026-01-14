package com.fourshil.musicya.di;

import com.fourshil.musicya.data.local.MusicDao;
import com.fourshil.musicya.data.local.MusicDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideMusicDaoFactory implements Factory<MusicDao> {
  private final Provider<MusicDatabase> databaseProvider;

  public DatabaseModule_ProvideMusicDaoFactory(Provider<MusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MusicDao get() {
    return provideMusicDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMusicDaoFactory create(
      Provider<MusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMusicDaoFactory(databaseProvider);
  }

  public static MusicDao provideMusicDao(MusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMusicDao(database));
  }
}
