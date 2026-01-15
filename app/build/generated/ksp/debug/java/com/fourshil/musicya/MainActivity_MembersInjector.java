package com.fourshil.musicya;

import com.fourshil.musicya.data.SettingsPreferences;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingsPreferences> settingsPreferencesProvider;

  public MainActivity_MembersInjector(Provider<SettingsPreferences> settingsPreferencesProvider) {
    this.settingsPreferencesProvider = settingsPreferencesProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsPreferences> settingsPreferencesProvider) {
    return new MainActivity_MembersInjector(settingsPreferencesProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsPreferences(instance, settingsPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.MainActivity.settingsPreferences")
  public static void injectSettingsPreferences(MainActivity instance,
      SettingsPreferences settingsPreferences) {
    instance.settingsPreferences = settingsPreferences;
  }
}
