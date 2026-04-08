package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.CivicPublisher;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.local.dao.ManifestoDao;
import net.wetheGoverned.remote.api.CivicApi;

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
public final class ManifestoRepositoryImpl_Factory implements Factory<ManifestoRepositoryImpl> {
  private final Provider<ManifestoDao> manifestoDaoProvider;

  private final Provider<CivicApi> civicApiProvider;

  private final Provider<CivicPublisher> civicPublisherProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public ManifestoRepositoryImpl_Factory(Provider<ManifestoDao> manifestoDaoProvider,
      Provider<CivicApi> civicApiProvider, Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.manifestoDaoProvider = manifestoDaoProvider;
    this.civicApiProvider = civicApiProvider;
    this.civicPublisherProvider = civicPublisherProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public ManifestoRepositoryImpl get() {
    return newInstance(manifestoDaoProvider.get(), civicApiProvider.get(), civicPublisherProvider.get(), dispatchersProvider.get());
  }

  public static ManifestoRepositoryImpl_Factory create(Provider<ManifestoDao> manifestoDaoProvider,
      Provider<CivicApi> civicApiProvider, Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new ManifestoRepositoryImpl_Factory(manifestoDaoProvider, civicApiProvider, civicPublisherProvider, dispatchersProvider);
  }

  public static ManifestoRepositoryImpl newInstance(ManifestoDao manifestoDao, CivicApi civicApi,
      CivicPublisher civicPublisher, DispatcherProvider dispatchers) {
    return new ManifestoRepositoryImpl(manifestoDao, civicApi, civicPublisher, dispatchers);
  }
}
