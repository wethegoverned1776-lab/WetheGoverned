package net.wetheGoverned.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.CivicDatabase;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CivicDatabaseModule_ProvideDatabaseFactory implements Factory<CivicDatabase> {
  private final Provider<Context> contextProvider;

  public CivicDatabaseModule_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CivicDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static CivicDatabaseModule_ProvideDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new CivicDatabaseModule_ProvideDatabaseFactory(contextProvider);
  }

  public static CivicDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(CivicDatabaseModule.INSTANCE.provideDatabase(context));
  }
}
