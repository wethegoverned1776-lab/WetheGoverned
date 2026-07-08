package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.remote.api.WtgBackendApi;

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
public final class CivicNetworkModule_ProvideWtgBackendApiFactory implements Factory<WtgBackendApi> {
  private final Provider<HttpClient> httpClientProvider;

  public CivicNetworkModule_ProvideWtgBackendApiFactory(Provider<HttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public WtgBackendApi get() {
    return provideWtgBackendApi(httpClientProvider.get());
  }

  public static CivicNetworkModule_ProvideWtgBackendApiFactory create(
      Provider<HttpClient> httpClientProvider) {
    return new CivicNetworkModule_ProvideWtgBackendApiFactory(httpClientProvider);
  }

  public static WtgBackendApi provideWtgBackendApi(HttpClient httpClient) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideWtgBackendApi(httpClient));
  }
}
