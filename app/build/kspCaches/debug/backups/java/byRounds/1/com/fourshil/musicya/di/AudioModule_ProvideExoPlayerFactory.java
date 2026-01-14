package com.fourshil.musicya.di;

import android.content.Context;
import androidx.media3.common.AudioAttributes;
import androidx.media3.exoplayer.ExoPlayer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AudioModule_ProvideExoPlayerFactory implements Factory<ExoPlayer> {
  private final Provider<Context> contextProvider;

  private final Provider<AudioAttributes> audioAttributesProvider;

  public AudioModule_ProvideExoPlayerFactory(Provider<Context> contextProvider,
      Provider<AudioAttributes> audioAttributesProvider) {
    this.contextProvider = contextProvider;
    this.audioAttributesProvider = audioAttributesProvider;
  }

  @Override
  public ExoPlayer get() {
    return provideExoPlayer(contextProvider.get(), audioAttributesProvider.get());
  }

  public static AudioModule_ProvideExoPlayerFactory create(Provider<Context> contextProvider,
      Provider<AudioAttributes> audioAttributesProvider) {
    return new AudioModule_ProvideExoPlayerFactory(contextProvider, audioAttributesProvider);
  }

  public static ExoPlayer provideExoPlayer(Context context, AudioAttributes audioAttributes) {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.provideExoPlayer(context, audioAttributes));
  }
}
