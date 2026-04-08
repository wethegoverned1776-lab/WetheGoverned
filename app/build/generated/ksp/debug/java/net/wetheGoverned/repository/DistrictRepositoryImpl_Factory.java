package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.local.dao.DistrictDao;
import net.wetheGoverned.local.dao.MetricDao;
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
public final class DistrictRepositoryImpl_Factory implements Factory<DistrictRepositoryImpl> {
  private final Provider<DistrictDao> districtDaoProvider;

  private final Provider<MetricDao> metricDaoProvider;

  private final Provider<CivicApi> civicApiProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public DistrictRepositoryImpl_Factory(Provider<DistrictDao> districtDaoProvider,
      Provider<MetricDao> metricDaoProvider, Provider<CivicApi> civicApiProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.districtDaoProvider = districtDaoProvider;
    this.metricDaoProvider = metricDaoProvider;
    this.civicApiProvider = civicApiProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public DistrictRepositoryImpl get() {
    return newInstance(districtDaoProvider.get(), metricDaoProvider.get(), civicApiProvider.get(), dispatchersProvider.get());
  }

  public static DistrictRepositoryImpl_Factory create(Provider<DistrictDao> districtDaoProvider,
      Provider<MetricDao> metricDaoProvider, Provider<CivicApi> civicApiProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new DistrictRepositoryImpl_Factory(districtDaoProvider, metricDaoProvider, civicApiProvider, dispatchersProvider);
  }

  public static DistrictRepositoryImpl newInstance(DistrictDao districtDao, MetricDao metricDao,
      CivicApi civicApi, DispatcherProvider dispatchers) {
    return new DistrictRepositoryImpl(districtDao, metricDao, civicApi, dispatchers);
  }
}
