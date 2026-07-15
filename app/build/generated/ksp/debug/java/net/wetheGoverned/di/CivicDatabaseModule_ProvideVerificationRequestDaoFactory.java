package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.CivicDatabase;
import net.wetheGoverned.local.dao.VerificationRequestDao;

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
public final class CivicDatabaseModule_ProvideVerificationRequestDaoFactory implements Factory<VerificationRequestDao> {
  private final Provider<CivicDatabase> dbProvider;

  public CivicDatabaseModule_ProvideVerificationRequestDaoFactory(
      Provider<CivicDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VerificationRequestDao get() {
    return provideVerificationRequestDao(dbProvider.get());
  }

  public static CivicDatabaseModule_ProvideVerificationRequestDaoFactory create(
      Provider<CivicDatabase> dbProvider) {
    return new CivicDatabaseModule_ProvideVerificationRequestDaoFactory(dbProvider);
  }

  public static VerificationRequestDao provideVerificationRequestDao(CivicDatabase db) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.provideVerificationRequestDao(db));
  }
}
