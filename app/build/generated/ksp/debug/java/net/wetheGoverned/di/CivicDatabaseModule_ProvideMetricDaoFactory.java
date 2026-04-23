package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.CivicDatabase;
import net.wetheGoverned.local.dao.MetricDao;

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
public final class CivicDatabaseModule_ProvideMetricDaoFactory implements Factory<MetricDao> {
  private final Provider<CivicDatabase> dbProvider;

  public CivicDatabaseModule_ProvideMetricDaoFactory(Provider<CivicDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MetricDao get() {
    return provideMetricDao(dbProvider.get());
  }

  public static CivicDatabaseModule_ProvideMetricDaoFactory create(
      Provider<CivicDatabase> dbProvider) {
    return new CivicDatabaseModule_ProvideMetricDaoFactory(dbProvider);
  }

  public static MetricDao provideMetricDao(CivicDatabase db) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.provideMetricDao(db));
  }
}
