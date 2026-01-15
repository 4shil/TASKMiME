package com.fourshil.musicya.ui.library;

import com.fourshil.musicya.data.db.MusicDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PlaylistsViewModel_Factory implements Factory<PlaylistsViewModel> {
  private final Provider<MusicDao> musicDaoProvider;

  public PlaylistsViewModel_Factory(Provider<MusicDao> musicDaoProvider) {
    this.musicDaoProvider = musicDaoProvider;
  }

  @Override
  public PlaylistsViewModel get() {
    return newInstance(musicDaoProvider.get());
  }

  public static PlaylistsViewModel_Factory create(Provider<MusicDao> musicDaoProvider) {
    return new PlaylistsViewModel_Factory(musicDaoProvider);
  }

  public static PlaylistsViewModel newInstance(MusicDao musicDao) {
    return new PlaylistsViewModel(musicDao);
  }
}
