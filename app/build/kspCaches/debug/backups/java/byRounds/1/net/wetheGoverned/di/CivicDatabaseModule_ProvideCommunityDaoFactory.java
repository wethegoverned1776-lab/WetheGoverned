package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.CivicDatabase;
import net.wetheGoverned.local.dao.CommunityPostDao;

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
public final class CivicDatabaseModule_ProvideCommunityDaoFactory implements Factory<CommunityPostDao> {
  private final Provider<CivicDatabase> dbProvider;

  public CivicDatabaseModule_ProvideCommunityDaoFactory(Provider<CivicDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CommunityPostDao get() {
    return provideCommunityDao(dbProvider.get());
  }

  public static CivicDatabaseModule_ProvideCommunityDaoFactory create(
      Provider<CivicDatabase> dbProvider) {
    return new CivicDatabaseModule_ProvideCommunityDaoFactory(dbProvider);
  }

  public static CommunityPostDao provideCommunityDao(CivicDatabase db) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.provideCommunityDao(db));
  }
}
