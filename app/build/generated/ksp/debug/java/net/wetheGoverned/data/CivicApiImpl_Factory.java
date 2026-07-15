package net.wetheGoverned.data;

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
public final class CivicApiImpl_Factory implements Factory<CivicApiImpl> {
  @Override
  public CivicApiImpl get() {
    return newInstance();
  }

  public static CivicApiImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CivicApiImpl newInstance() {
    return new CivicApiImpl();
  }

  private static final class InstanceHolder {
    private static final CivicApiImpl_Factory INSTANCE = new CivicApiImpl_Factory();
  }
}
