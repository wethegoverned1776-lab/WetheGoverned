package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.core.WsCivicPublisherWithQueue;
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
public final class CivicNetworkModule_ProvideWsCivicPublisherFactory implements Factory<WsCivicPublisherWithQueue> {
  private final Provider<List<String>> relayUrlsProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private final Provider<PendingEventQueue> pendingQueueProvider;

  private final Provider<ZkProver> zkProverProvider;

  public CivicNetworkModule_ProvideWsCivicPublisherFactory(Provider<List<String>> relayUrlsProvider,
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
    return provideWsCivicPublisher(relayUrlsProvider.get(), sessionManagerProvider.get(), dispatchersProvider.get(), pendingQueueProvider.get(), zkProverProvider.get());
  }

  public static CivicNetworkModule_ProvideWsCivicPublisherFactory create(
      Provider<List<String>> relayUrlsProvider, Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider, Provider<ZkProver> zkProverProvider) {
    return new CivicNetworkModule_ProvideWsCivicPublisherFactory(relayUrlsProvider, sessionManagerProvider, dispatchersProvider, pendingQueueProvider, zkProverProvider);
  }

  public static WsCivicPublisherWithQueue provideWsCivicPublisher(List<String> relayUrls,
      SessionManager sessionManager, DispatcherProvider dispatchers, PendingEventQueue pendingQueue,
      ZkProver zkProver) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideWsCivicPublisher(relayUrls, sessionManager, dispatchers, pendingQueue, zkProver));
  }
}
