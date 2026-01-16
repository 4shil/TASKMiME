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
public final class PlayerController_Factory implements Factory<PlayerController> {
  private final Provider<Context> contextProvider;

  public PlayerController_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PlayerController get() {
    return newInstance(contextProvider.get());
  }

  public static PlayerController_Factory create(Provider<Context> contextProvider) {
    return new PlayerController_Factory(contextProvider);
  }

  public static PlayerController newInstance(Context context) {
    return new PlayerController(context);
  }
}
