package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
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
  @Override
  public CivicApi get() {
    return provideCivicApi();
  }

  public static CivicNetworkModule_ProvideCivicApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CivicApi provideCivicApi() {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideCivicApi());
  }

  private static final class InstanceHolder {
    private static final CivicNetworkModule_ProvideCivicApiFactory INSTANCE = new CivicNetworkModule_ProvideCivicApiFactory();
  }
}
