package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.local.dao.ResidentProfileDao;
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
public final class ResidentRepositoryImpl_Factory implements Factory<ResidentRepositoryImpl> {
  private final Provider<ResidentProfileDao> profileDaoProvider;

  private final Provider<CivicApi> civicApiProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public ResidentRepositoryImpl_Factory(Provider<ResidentProfileDao> profileDaoProvider,
      Provider<CivicApi> civicApiProvider, Provider<DispatcherProvider> dispatchersProvider) {
    this.profileDaoProvider = profileDaoProvider;
    this.civicApiProvider = civicApiProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public ResidentRepositoryImpl get() {
    return newInstance(profileDaoProvider.get(), civicApiProvider.get(), dispatchersProvider.get());
  }

  public static ResidentRepositoryImpl_Factory create(
      Provider<ResidentProfileDao> profileDaoProvider, Provider<CivicApi> civicApiProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new ResidentRepositoryImpl_Factory(profileDaoProvider, civicApiProvider, dispatchersProvider);
  }

  public static ResidentRepositoryImpl newInstance(ResidentProfileDao profileDao, CivicApi civicApi,
      DispatcherProvider dispatchers) {
    return new ResidentRepositoryImpl(profileDao, civicApi, dispatchers);
  }
}
