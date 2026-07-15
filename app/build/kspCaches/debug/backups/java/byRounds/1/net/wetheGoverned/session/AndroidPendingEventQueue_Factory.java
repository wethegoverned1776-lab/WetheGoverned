package net.wetheGoverned.session;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.dao.PendingEventDao;

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
public final class AndroidPendingEventQueue_Factory implements Factory<AndroidPendingEventQueue> {
  private final Provider<PendingEventDao> pendingEventDaoProvider;

  public AndroidPendingEventQueue_Factory(Provider<PendingEventDao> pendingEventDaoProvider) {
    this.pendingEventDaoProvider = pendingEventDaoProvider;
  }

  @Override
  public AndroidPendingEventQueue get() {
    return newInstance(pendingEventDaoProvider.get());
  }

  public static AndroidPendingEventQueue_Factory create(
      Provider<PendingEventDao> pendingEventDaoProvider) {
    return new AndroidPendingEventQueue_Factory(pendingEventDaoProvider);
  }

  public static AndroidPendingEventQueue newInstance(PendingEventDao pendingEventDao) {
    return new AndroidPendingEventQueue(pendingEventDao);
  }
}
