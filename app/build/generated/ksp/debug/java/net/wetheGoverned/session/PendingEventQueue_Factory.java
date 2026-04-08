package net.wetheGoverned.session;

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
public final class PendingEventQueue_Factory implements Factory<PendingEventQueue> {
  @Override
  public PendingEventQueue get() {
    return newInstance();
  }

  public static PendingEventQueue_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PendingEventQueue newInstance() {
    return new PendingEventQueue();
  }

  private static final class InstanceHolder {
    private static final PendingEventQueue_Factory INSTANCE = new PendingEventQueue_Factory();
  }
}
