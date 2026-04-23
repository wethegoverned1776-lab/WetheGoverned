package net.wetheGoverned.core;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.session.PendingEventQueue;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.zk.ZkProver;

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
  private final Provider<List<String>> relayUrlsProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private final Provider<PendingEventQueue> pendingQueueProvider;

  private final Provider<ZkProver> zkProverProvider;

  public WsCivicPublisherWithQueue_Factory(Provider<List<String>> relayUrlsProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider, Provider<ZkProver> zkProverProvider) {
    this.relayUrlsProvider = relayUrlsProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.dispatchersProvider = dispatchersProvider;
    this.pendingQueueProvider = pendingQueueProvider;
    this.zkProverProvider = zkProverProvider;
  }

  @Override
  public WsCivicPublisherWithQueue get() {
    return newInstance(relayUrlsProvider.get(), sessionManagerProvider.get(), dispatchersProvider.get(), pendingQueueProvider.get(), zkProverProvider.get());
  }

  public static WsCivicPublisherWithQueue_Factory create(Provider<List<String>> relayUrlsProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider, Provider<ZkProver> zkProverProvider) {
    return new WsCivicPublisherWithQueue_Factory(relayUrlsProvider, sessionManagerProvider, dispatchersProvider, pendingQueueProvider, zkProverProvider);
  }

  public static WsCivicPublisherWithQueue newInstance(List<String> relayUrls,
      SessionManager sessionManager, DispatcherProvider dispatchers, PendingEventQueue pendingQueue,
      ZkProver zkProver) {
    return new WsCivicPublisherWithQueue(relayUrls, sessionManager, dispatchers, pendingQueue, zkProver);
  }
}
