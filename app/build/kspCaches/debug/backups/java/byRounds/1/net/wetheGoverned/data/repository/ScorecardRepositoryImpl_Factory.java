package net.wetheGoverned.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ScorecardRepositoryImpl_Factory implements Factory<ScorecardRepositoryImpl> {
  @Override
  public ScorecardRepositoryImpl get() {
    return newInstance();
  }

  public static ScorecardRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ScorecardRepositoryImpl newInstance() {
    return new ScorecardRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final ScorecardRepositoryImpl_Factory INSTANCE = new ScorecardRepositoryImpl_Factory();
  }
}
