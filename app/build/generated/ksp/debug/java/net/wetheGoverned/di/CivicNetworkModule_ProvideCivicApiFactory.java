package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.List;
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
  private final Provider<List<String>> relayUrlsProvider;

  public CivicNetworkModule_ProvideCivicApiFactory(Provider<List<String>> relayUrlsProvider) {
    this.relayUrlsProvider = relayUrlsProvider;
  }

  @Override
  public CivicApi get() {
    return provideCivicApi(relayUrlsProvider.get());
  }

  public static CivicNetworkModule_ProvideCivicApiFactory create(
      Provider<List<String>> relayUrlsProvider) {
    return new CivicNetworkModule_ProvideCivicApiFactory(relayUrlsProvider);
  }

  public static CivicApi provideCivicApi(List<String> relayUrls) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideCivicApi(relayUrls));
  }
}
