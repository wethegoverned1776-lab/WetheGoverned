package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.CivicPublisher;
import net.wetheGoverned.data.NostrRelayManager;
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
public final class CivicNetworkModule_ProvideWsCivicPublisherFactory implements Factory<CivicPublisher> {
  private final Provider<NostrRelayManager> relayManagerProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<PendingEventQueue> pendingQueueProvider;

  private final Provider<ZkProver> zkProverProvider;

  public CivicNetworkModule_ProvideWsCivicPublisherFactory(
      Provider<NostrRelayManager> relayManagerProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<PendingEventQueue> pendingQueueProvider, Provider<ZkProver> zkProverProvider) {
    this.relayManagerProvider = relayManagerProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.pendingQueueProvider = pendingQueueProvider;
    this.zkProverProvider = zkProverProvider;
  }

  @Override
  public CivicPublisher get() {
    return provideWsCivicPublisher(relayManagerProvider.get(), sessionManagerProvider.get(), pendingQueueProvider.get(), zkProverProvider.get());
  }

  public static CivicNetworkModule_ProvideWsCivicPublisherFactory create(
      Provider<NostrRelayManager> relayManagerProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<PendingEventQueue> pendingQueueProvider, Provider<ZkProver> zkProverProvider) {
    return new CivicNetworkModule_ProvideWsCivicPublisherFactory(relayManagerProvider, sessionManagerProvider, pendingQueueProvider, zkProverProvider);
  }

  public static CivicPublisher provideWsCivicPublisher(NostrRelayManager relayManager,
      SessionManager sessionManager, PendingEventQueue pendingQueue, ZkProver zkProver) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideWsCivicPublisher(relayManager, sessionManager, pendingQueue, zkProver));
  }
}
