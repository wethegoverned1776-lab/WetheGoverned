package net.wetheGoverned.data;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class P2PService_MembersInjector implements MembersInjector<P2PService> {
  private final Provider<P2PSyncEngine> p2pSyncEngineProvider;

  public P2PService_MembersInjector(Provider<P2PSyncEngine> p2pSyncEngineProvider) {
    this.p2pSyncEngineProvider = p2pSyncEngineProvider;
  }

  public static MembersInjector<P2PService> create(Provider<P2PSyncEngine> p2pSyncEngineProvider) {
    return new P2PService_MembersInjector(p2pSyncEngineProvider);
  }

  @Override
  public void injectMembers(P2PService instance) {
    injectP2pSyncEngine(instance, p2pSyncEngineProvider.get());
  }

  @InjectedFieldSignature("net.wetheGoverned.data.P2PService.p2pSyncEngine")
  public static void injectP2pSyncEngine(P2PService instance, P2PSyncEngine p2pSyncEngine) {
    instance.p2pSyncEngine = p2pSyncEngine;
  }
}
