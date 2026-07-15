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
public final class DistrictRepositoryImpl_Factory implements Factory<DistrictRepositoryImpl> {
  @Override
  public DistrictRepositoryImpl get() {
    return newInstance();
  }

  public static DistrictRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DistrictRepositoryImpl newInstance() {
    return new DistrictRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final DistrictRepositoryImpl_Factory INSTANCE = new DistrictRepositoryImpl_Factory();
  }
}
