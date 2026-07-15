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
public final class PollRepositoryImpl_Factory implements Factory<PollRepositoryImpl> {
  @Override
  public PollRepositoryImpl get() {
    return newInstance();
  }

  public static PollRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PollRepositoryImpl newInstance() {
    return new PollRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final PollRepositoryImpl_Factory INSTANCE = new PollRepositoryImpl_Factory();
  }
}
