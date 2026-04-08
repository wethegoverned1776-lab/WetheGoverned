package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.CivicPublisher;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.local.dao.MetricDao;
import net.wetheGoverned.local.dao.ScorecardDao;
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
public final class ScorecardRepositoryImpl_Factory implements Factory<ScorecardRepositoryImpl> {
  private final Provider<ScorecardDao> scorecardDaoProvider;

  private final Provider<MetricDao> metricDaoProvider;

  private final Provider<CivicApi> civicApiProvider;

  private final Provider<CivicPublisher> civicPublisherProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public ScorecardRepositoryImpl_Factory(Provider<ScorecardDao> scorecardDaoProvider,
      Provider<MetricDao> metricDaoProvider, Provider<CivicApi> civicApiProvider,
      Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.scorecardDaoProvider = scorecardDaoProvider;
    this.metricDaoProvider = metricDaoProvider;
    this.civicApiProvider = civicApiProvider;
    this.civicPublisherProvider = civicPublisherProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public ScorecardRepositoryImpl get() {
    return newInstance(scorecardDaoProvider.get(), metricDaoProvider.get(), civicApiProvider.get(), civicPublisherProvider.get(), dispatchersProvider.get());
  }

  public static ScorecardRepositoryImpl_Factory create(Provider<ScorecardDao> scorecardDaoProvider,
      Provider<MetricDao> metricDaoProvider, Provider<CivicApi> civicApiProvider,
      Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new ScorecardRepositoryImpl_Factory(scorecardDaoProvider, metricDaoProvider, civicApiProvider, civicPublisherProvider, dispatchersProvider);
  }

  public static ScorecardRepositoryImpl newInstance(ScorecardDao scorecardDao, MetricDao metricDao,
      CivicApi civicApi, CivicPublisher civicPublisher, DispatcherProvider dispatchers) {
    return new ScorecardRepositoryImpl(scorecardDao, metricDao, civicApi, civicPublisher, dispatchers);
  }
}
