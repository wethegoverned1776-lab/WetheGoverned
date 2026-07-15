package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.CivicDatabase;
import net.wetheGoverned.local.dao.PendingEventDao;

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
public final class CivicDatabaseModule_ProvidePendingDaoFactory implements Factory<PendingEventDao> {
  private final Provider<CivicDatabase> dbProvider;

  public CivicDatabaseModule_ProvidePendingDaoFactory(Provider<CivicDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PendingEventDao get() {
    return providePendingDao(dbProvider.get());
  }

  public static CivicDatabaseModule_ProvidePendingDaoFactory create(
      Provider<CivicDatabase> dbProvider) {
    return new CivicDatabaseModule_ProvidePendingDaoFactory(dbProvider);
  }

  public static PendingEventDao providePendingDao(CivicDatabase db) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.providePendingDao(db));
  }
}
