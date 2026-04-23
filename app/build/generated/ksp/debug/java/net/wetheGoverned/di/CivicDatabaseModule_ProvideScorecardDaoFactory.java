package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.CivicDatabase;
import net.wetheGoverned.local.dao.ScorecardDao;

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
public final class CivicDatabaseModule_ProvideScorecardDaoFactory implements Factory<ScorecardDao> {
  private final Provider<CivicDatabase> dbProvider;

  public CivicDatabaseModule_ProvideScorecardDaoFactory(Provider<CivicDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ScorecardDao get() {
    return provideScorecardDao(dbProvider.get());
  }

  public static CivicDatabaseModule_ProvideScorecardDaoFactory create(
      Provider<CivicDatabase> dbProvider) {
    return new CivicDatabaseModule_ProvideScorecardDaoFactory(dbProvider);
  }

  public static ScorecardDao provideScorecardDao(CivicDatabase db) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.provideScorecardDao(db));
  }
}
