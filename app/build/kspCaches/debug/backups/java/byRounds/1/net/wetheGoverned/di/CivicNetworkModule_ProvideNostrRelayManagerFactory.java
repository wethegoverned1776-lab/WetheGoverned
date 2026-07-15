package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.NostrRelayManager;

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
public final class CivicNetworkModule_ProvideNostrRelayManagerFactory implements Factory<NostrRelayManager> {
  private final Provider<List<String>> relayUrlsProvider;

  public CivicNetworkModule_ProvideNostrRelayManagerFactory(
      Provider<List<String>> relayUrlsProvider) {
    this.relayUrlsProvider = relayUrlsProvider;
  }

  @Override
  public NostrRelayManager get() {
    return provideNostrRelayManager(relayUrlsProvider.get());
  }

  public static CivicNetworkModule_ProvideNostrRelayManagerFactory create(
      Provider<List<String>> relayUrlsProvider) {
    return new CivicNetworkModule_ProvideNostrRelayManagerFactory(relayUrlsProvider);
  }

  public static NostrRelayManager provideNostrRelayManager(List<String> relayUrls) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideNostrRelayManager(relayUrls));
  }
}
