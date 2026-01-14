package com.fourshil.musicya.di;

import androidx.media3.common.AudioAttributes;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AudioModule_ProvideAudioAttributesFactory implements Factory<AudioAttributes> {
  @Override
  public AudioAttributes get() {
    return provideAudioAttributes();
  }

  public static AudioModule_ProvideAudioAttributesFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioAttributes provideAudioAttributes() {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.provideAudioAttributes());
  }

  private static final class InstanceHolder {
    private static final AudioModule_ProvideAudioAttributesFactory INSTANCE = new AudioModule_ProvideAudioAttributesFactory();
  }
}
