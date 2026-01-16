package com.fourshil.musicya.data;

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
public final class SettingsPreferences_Factory implements Factory<SettingsPreferences> {
  private final Provider<Context> contextProvider;

  public SettingsPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingsPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static SettingsPreferences_Factory create(Provider<Context> contextProvider) {
    return new SettingsPreferences_Factory(contextProvider);
  }

  public static SettingsPreferences newInstance(Context context) {
    return new SettingsPreferences(context);
  }
}
