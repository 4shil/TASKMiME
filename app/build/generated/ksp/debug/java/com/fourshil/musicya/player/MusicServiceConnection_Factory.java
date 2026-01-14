package com.fourshil.musicya.player;

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
public final class MusicServiceConnection_Factory implements Factory<MusicServiceConnection> {
  private final Provider<Context> contextProvider;

  public MusicServiceConnection_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MusicServiceConnection get() {
    return newInstance(contextProvider.get());
  }

  public static MusicServiceConnection_Factory create(Provider<Context> contextProvider) {
    return new MusicServiceConnection_Factory(contextProvider);
  }

  public static MusicServiceConnection newInstance(Context context) {
    return new MusicServiceConnection(context);
  }
}
