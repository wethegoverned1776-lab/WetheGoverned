package net.wetheGoverned;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.P2PSyncEngine;

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
public final class WeTheGovernedApplication_MembersInjector implements MembersInjector<WeTheGovernedApplication> {
  private final Provider<P2PSyncEngine> p2pSyncEngineProvider;

  public WeTheGovernedApplication_MembersInjector(Provider<P2PSyncEngine> p2pSyncEngineProvider) {
    this.p2pSyncEngineProvider = p2pSyncEngineProvider;
  }

  public static MembersInjector<WeTheGovernedApplication> create(
      Provider<P2PSyncEngine> p2pSyncEngineProvider) {
    return new WeTheGovernedApplication_MembersInjector(p2pSyncEngineProvider);
  }

  @Override
  public void injectMembers(WeTheGovernedApplication instance) {
    injectP2pSyncEngine(instance, p2pSyncEngineProvider.get());
  }

  @InjectedFieldSignature("net.wetheGoverned.WeTheGovernedApplication.p2pSyncEngine")
  public static void injectP2pSyncEngine(WeTheGovernedApplication instance,
      P2PSyncEngine p2pSyncEngine) {
    instance.p2pSyncEngine = p2pSyncEngine;
  }
}
