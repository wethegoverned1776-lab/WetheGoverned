package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.remote.api.CivicApi;

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
    "KotlinInternalInJava",
    "cast"
})
public final class CivicNetworkModule_ProvideCivicApiFactory implements Factory<CivicApi> {
  private final Provider<String> relayUrlProvider;

  public CivicNetworkModule_ProvideCivicApiFactory(Provider<String> relayUrlProvider) {
    this.relayUrlProvider = relayUrlProvider;
  }

  @Override
  public CivicApi get() {
    return provideCivicApi(relayUrlProvider.get());
  }

  public static CivicNetworkModule_ProvideCivicApiFactory create(
      Provider<String> relayUrlProvider) {
    return new CivicNetworkModule_ProvideCivicApiFactory(relayUrlProvider);
  }

  public static CivicApi provideCivicApi(String relayUrl) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideCivicApi(relayUrl));
  }
}
