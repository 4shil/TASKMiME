package com.fourshil.musicya.lyrics;

import com.fourshil.musicya.player.MusicServiceConnection;
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
public final class LyricsViewModel_Factory implements Factory<LyricsViewModel> {
  private final Provider<LyricsRepository> lyricsRepositoryProvider;

  private final Provider<MusicServiceConnection> serviceConnectionProvider;

  public LyricsViewModel_Factory(Provider<LyricsRepository> lyricsRepositoryProvider,
      Provider<MusicServiceConnection> serviceConnectionProvider) {
    this.lyricsRepositoryProvider = lyricsRepositoryProvider;
    this.serviceConnectionProvider = serviceConnectionProvider;
  }

  @Override
  public LyricsViewModel get() {
    return newInstance(lyricsRepositoryProvider.get(), serviceConnectionProvider.get());
  }

  public static LyricsViewModel_Factory create(Provider<LyricsRepository> lyricsRepositoryProvider,
      Provider<MusicServiceConnection> serviceConnectionProvider) {
    return new LyricsViewModel_Factory(lyricsRepositoryProvider, serviceConnectionProvider);
  }

  public static LyricsViewModel newInstance(LyricsRepository lyricsRepository,
      MusicServiceConnection serviceConnection) {
    return new LyricsViewModel(lyricsRepository, serviceConnection);
  }
}
