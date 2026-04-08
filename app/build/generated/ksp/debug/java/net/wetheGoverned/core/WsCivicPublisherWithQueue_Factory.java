package net.wetheGoverned.core;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.session.PendingEventQueue;
import net.wetheGoverned.session.SessionManager;

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
public final class WsCivicPublisherWithQueue_Factory implements Factory<WsCivicPublisherWithQueue> {
  private final Provider<String> relayUrlProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private final Provider<PendingEventQueue> pendingQueueProvider;

  public WsCivicPublisherWithQueue_Factory(Provider<String> relayUrlProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider) {
    this.relayUrlProvider = relayUrlProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.dispatchersProvider = dispatchersProvider;
    this.pendingQueueProvider = pendingQueueProvider;
  }

  @Override
  public WsCivicPublisherWithQueue get() {
    return newInstance(relayUrlProvider.get(), sessionManagerProvider.get(), dispatchersProvider.get(), pendingQueueProvider.get());
  }

  public static WsCivicPublisherWithQueue_Factory create(Provider<String> relayUrlProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider) {
    return new WsCivicPublisherWithQueue_Factory(relayUrlProvider, sessionManagerProvider, dispatchersProvider, pendingQueueProvider);
  }

  public static WsCivicPublisherWithQueue newInstance(String relayUrl,
      SessionManager sessionManager, DispatcherProvider dispatchers,
      PendingEventQueue pendingQueue) {
    return new WsCivicPublisherWithQueue(relayUrl, sessionManager, dispatchers, pendingQueue);
  }
}
