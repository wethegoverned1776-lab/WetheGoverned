package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.core.WsCivicPublisherWithQueue;
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
public final class CivicNetworkModule_ProvideWsCivicPublisherFactory implements Factory<WsCivicPublisherWithQueue> {
  private final Provider<String> relayUrlProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private final Provider<PendingEventQueue> pendingQueueProvider;

  public CivicNetworkModule_ProvideWsCivicPublisherFactory(Provider<String> relayUrlProvider,
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
    return provideWsCivicPublisher(relayUrlProvider.get(), sessionManagerProvider.get(), dispatchersProvider.get(), pendingQueueProvider.get());
  }

  public static CivicNetworkModule_ProvideWsCivicPublisherFactory create(
      Provider<String> relayUrlProvider, Provider<SessionManager> sessionManagerProvider,
      Provider<DispatcherProvider> dispatchersProvider,
      Provider<PendingEventQueue> pendingQueueProvider) {
    return new CivicNetworkModule_ProvideWsCivicPublisherFactory(relayUrlProvider, sessionManagerProvider, dispatchersProvider, pendingQueueProvider);
  }

  public static WsCivicPublisherWithQueue provideWsCivicPublisher(String relayUrl,
      SessionManager sessionManager, DispatcherProvider dispatchers,
      PendingEventQueue pendingQueue) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideWsCivicPublisher(relayUrl, sessionManager, dispatchers, pendingQueue));
  }
}
