package com.fourshil.musicya.ui.nowplaying;

import com.fourshil.musicya.data.db.MusicDao;
import com.fourshil.musicya.player.PlayerController;
import com.fourshil.musicya.util.AlbumArtHelper;
import com.fourshil.musicya.util.LyricsManager;
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
public final class NowPlayingViewModel_Factory implements Factory<NowPlayingViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<LyricsManager> lyricsManagerProvider;

  private final Provider<AlbumArtHelper> albumArtHelperProvider;

  public NowPlayingViewModel_Factory(Provider<PlayerController> playerControllerProvider,
      Provider<MusicDao> musicDaoProvider, Provider<LyricsManager> lyricsManagerProvider,
      Provider<AlbumArtHelper> albumArtHelperProvider) {
    this.playerControllerProvider = playerControllerProvider;
    this.musicDaoProvider = musicDaoProvider;
    this.lyricsManagerProvider = lyricsManagerProvider;
    this.albumArtHelperProvider = albumArtHelperProvider;
  }

  @Override
  public NowPlayingViewModel get() {
    return newInstance(playerControllerProvider.get(), musicDaoProvider.get(), lyricsManagerProvider.get(), albumArtHelperProvider.get());
  }

  public static NowPlayingViewModel_Factory create(
      Provider<PlayerController> playerControllerProvider, Provider<MusicDao> musicDaoProvider,
      Provider<LyricsManager> lyricsManagerProvider,
      Provider<AlbumArtHelper> albumArtHelperProvider) {
    return new NowPlayingViewModel_Factory(playerControllerProvider, musicDaoProvider, lyricsManagerProvider, albumArtHelperProvider);
  }

  public static NowPlayingViewModel newInstance(PlayerController playerController,
      MusicDao musicDao, LyricsManager lyricsManager, AlbumArtHelper albumArtHelper) {
    return new NowPlayingViewModel(playerController, musicDao, lyricsManager, albumArtHelper);
  }
}
