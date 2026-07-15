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
public final class VoteRepositoryImpl_Factory implements Factory<VoteRepositoryImpl> {
  @Override
  public VoteRepositoryImpl get() {
    return newInstance();
  }

  public static VoteRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VoteRepositoryImpl newInstance() {
    return new VoteRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final VoteRepositoryImpl_Factory INSTANCE = new VoteRepositoryImpl_Factory();
  }
}
