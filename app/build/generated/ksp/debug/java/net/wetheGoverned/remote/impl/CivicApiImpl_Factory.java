package net.wetheGoverned.remote.impl;

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
    "KotlinInternalInJava",
    "cast"
})
public final class CivicApiImpl_Factory implements Factory<CivicApiImpl> {
  private final Provider<String> congressApiKeyProvider;

  private final Provider<String> openStatesApiKeyProvider;

  private final Provider<String> googleCivicApiKeyProvider;

  private final Provider<String> relayUrlProvider;

  public CivicApiImpl_Factory(Provider<String> congressApiKeyProvider,
      Provider<String> openStatesApiKeyProvider, Provider<String> googleCivicApiKeyProvider,
      Provider<String> relayUrlProvider) {
    this.congressApiKeyProvider = congressApiKeyProvider;
    this.openStatesApiKeyProvider = openStatesApiKeyProvider;
    this.googleCivicApiKeyProvider = googleCivicApiKeyProvider;
    this.relayUrlProvider = relayUrlProvider;
  }

  @Override
  public CivicApiImpl get() {
    return newInstance(congressApiKeyProvider.get(), openStatesApiKeyProvider.get(), googleCivicApiKeyProvider.get(), relayUrlProvider.get());
  }

  public static CivicApiImpl_Factory create(Provider<String> congressApiKeyProvider,
      Provider<String> openStatesApiKeyProvider, Provider<String> googleCivicApiKeyProvider,
      Provider<String> relayUrlProvider) {
    return new CivicApiImpl_Factory(congressApiKeyProvider, openStatesApiKeyProvider, googleCivicApiKeyProvider, relayUrlProvider);
  }

  public static CivicApiImpl newInstance(String congressApiKey, String openStatesApiKey,
      String googleCivicApiKey, String relayUrl) {
    return new CivicApiImpl(congressApiKey, openStatesApiKey, googleCivicApiKey, relayUrl);
  }
}
