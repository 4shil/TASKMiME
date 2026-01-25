package com.fourshil.musicya.ui.screens.settings;

import com.fourshil.musicya.data.SettingsPreferences;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsPreferences> settingsPreferencesProvider;

  public SettingsViewModel_Factory(Provider<SettingsPreferences> settingsPreferencesProvider) {
    this.settingsPreferencesProvider = settingsPreferencesProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsPreferencesProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<SettingsPreferences> settingsPreferencesProvider) {
    return new SettingsViewModel_Factory(settingsPreferencesProvider);
  }

  public static SettingsViewModel newInstance(SettingsPreferences settingsPreferences) {
    return new SettingsViewModel(settingsPreferences);
  }
}
