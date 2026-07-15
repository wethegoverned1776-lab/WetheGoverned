package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.List;
import javax.annotation.processing.Generated;

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
public final class CivicNetworkModule_ProvideRelayUrlsFactory implements Factory<List<String>> {
  @Override
  public List<String> get() {
    return provideRelayUrls();
  }

  public static CivicNetworkModule_ProvideRelayUrlsFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static List<String> provideRelayUrls() {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideRelayUrls());
  }

  private static final class InstanceHolder {
    private static final CivicNetworkModule_ProvideRelayUrlsFactory INSTANCE = new CivicNetworkModule_ProvideRelayUrlsFactory();
  }
}
