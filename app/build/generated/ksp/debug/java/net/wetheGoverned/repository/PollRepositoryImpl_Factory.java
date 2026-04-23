package net.wetheGoverned.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.dao.PollDao;
import net.wetheGoverned.local.dao.PollPostDao;

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
public final class PollRepositoryImpl_Factory implements Factory<PollRepositoryImpl> {
  private final Provider<PollDao> pollDaoProvider;

  private final Provider<PollPostDao> pollPostDaoProvider;

  public PollRepositoryImpl_Factory(Provider<PollDao> pollDaoProvider,
      Provider<PollPostDao> pollPostDaoProvider) {
    this.pollDaoProvider = pollDaoProvider;
    this.pollPostDaoProvider = pollPostDaoProvider;
  }

  @Override
  public PollRepositoryImpl get() {
    return newInstance(pollDaoProvider.get(), pollPostDaoProvider.get());
  }

  public static PollRepositoryImpl_Factory create(Provider<PollDao> pollDaoProvider,
      Provider<PollPostDao> pollPostDaoProvider) {
    return new PollRepositoryImpl_Factory(pollDaoProvider, pollPostDaoProvider);
  }

  public static PollRepositoryImpl newInstance(PollDao pollDao, PollPostDao pollPostDao) {
    return new PollRepositoryImpl(pollDao, pollPostDao);
  }
}
