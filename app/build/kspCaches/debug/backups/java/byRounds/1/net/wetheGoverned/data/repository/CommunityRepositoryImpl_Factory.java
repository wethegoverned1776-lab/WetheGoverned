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
public final class CommunityRepositoryImpl_Factory implements Factory<CommunityRepositoryImpl> {
  @Override
  public CommunityRepositoryImpl get() {
    return newInstance();
  }

  public static CommunityRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CommunityRepositoryImpl newInstance() {
    return new CommunityRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final CommunityRepositoryImpl_Factory INSTANCE = new CommunityRepositoryImpl_Factory();
  }
}
