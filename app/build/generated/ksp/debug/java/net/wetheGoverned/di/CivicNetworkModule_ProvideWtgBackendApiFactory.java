package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import net.wetheGoverned.remote.backend.WtgBackendApi;

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
  @Override
  public WtgBackendApi get() {
    return provideWtgBackendApi();
  }

  public static CivicNetworkModule_ProvideWtgBackendApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WtgBackendApi provideWtgBackendApi() {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideWtgBackendApi());
  }

  private static final class InstanceHolder {
    private static final CivicNetworkModule_ProvideWtgBackendApiFactory INSTANCE = new CivicNetworkModule_ProvideWtgBackendApiFactory();
  }
}
