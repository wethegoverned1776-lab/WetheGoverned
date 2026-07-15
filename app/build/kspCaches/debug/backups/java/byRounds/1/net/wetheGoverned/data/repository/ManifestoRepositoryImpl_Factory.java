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
public final class ManifestoRepositoryImpl_Factory implements Factory<ManifestoRepositoryImpl> {
  @Override
  public ManifestoRepositoryImpl get() {
    return newInstance();
  }

  public static ManifestoRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ManifestoRepositoryImpl newInstance() {
    return new ManifestoRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final ManifestoRepositoryImpl_Factory INSTANCE = new ManifestoRepositoryImpl_Factory();
  }
}
