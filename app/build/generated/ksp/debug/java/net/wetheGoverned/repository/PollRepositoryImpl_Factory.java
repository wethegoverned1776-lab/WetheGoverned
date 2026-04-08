package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.CivicPublisher;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.local.dao.PollDao;
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
public final class PollRepositoryImpl_Factory implements Factory<PollRepositoryImpl> {
  private final Provider<PollDao> pollDaoProvider;

  private final Provider<CivicApi> civicApiProvider;

  private final Provider<CivicPublisher> civicPublisherProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public PollRepositoryImpl_Factory(Provider<PollDao> pollDaoProvider,
      Provider<CivicApi> civicApiProvider, Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.pollDaoProvider = pollDaoProvider;
    this.civicApiProvider = civicApiProvider;
    this.civicPublisherProvider = civicPublisherProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public PollRepositoryImpl get() {
    return newInstance(pollDaoProvider.get(), civicApiProvider.get(), civicPublisherProvider.get(), dispatchersProvider.get());
  }

  public static PollRepositoryImpl_Factory create(Provider<PollDao> pollDaoProvider,
      Provider<CivicApi> civicApiProvider, Provider<CivicPublisher> civicPublisherProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new PollRepositoryImpl_Factory(pollDaoProvider, civicApiProvider, civicPublisherProvider, dispatchersProvider);
  }

  public static PollRepositoryImpl newInstance(PollDao pollDao, CivicApi civicApi,
      CivicPublisher civicPublisher, DispatcherProvider dispatchers) {
    return new PollRepositoryImpl(pollDao, civicApi, civicPublisher, dispatchers);
  }
}
