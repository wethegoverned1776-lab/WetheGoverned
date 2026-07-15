package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.NostrRelayManager;
import net.wetheGoverned.data.P2PSyncEngine;
import net.wetheGoverned.repository.AccountRepository;
import net.wetheGoverned.repository.ManifestoRepository;
import net.wetheGoverned.repository.PollRepository;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.repository.VoteRepository;
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
public final class CivicNetworkModule_ProvideP2PSyncEngineFactory implements Factory<P2PSyncEngine> {
  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<VoteRepository> voteRepositoryProvider;

  private final Provider<ManifestoRepository> manifestoRepositoryProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<NostrRelayManager> relayManagerProvider;

  public CivicNetworkModule_ProvideP2PSyncEngineFactory(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<VoteRepository> voteRepositoryProvider,
      Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<NostrRelayManager> relayManagerProvider) {
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.voteRepositoryProvider = voteRepositoryProvider;
    this.manifestoRepositoryProvider = manifestoRepositoryProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.relayManagerProvider = relayManagerProvider;
  }

  @Override
  public P2PSyncEngine get() {
    return provideP2PSyncEngine(pollRepositoryProvider.get(), residentRepositoryProvider.get(), voteRepositoryProvider.get(), manifestoRepositoryProvider.get(), accountRepositoryProvider.get(), sessionManagerProvider.get(), relayManagerProvider.get());
  }

  public static CivicNetworkModule_ProvideP2PSyncEngineFactory create(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<VoteRepository> voteRepositoryProvider,
      Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<NostrRelayManager> relayManagerProvider) {
    return new CivicNetworkModule_ProvideP2PSyncEngineFactory(pollRepositoryProvider, residentRepositoryProvider, voteRepositoryProvider, manifestoRepositoryProvider, accountRepositoryProvider, sessionManagerProvider, relayManagerProvider);
  }

  public static P2PSyncEngine provideP2PSyncEngine(PollRepository pollRepository,
      ResidentRepository residentRepository, VoteRepository voteRepository,
      ManifestoRepository manifestoRepository, AccountRepository accountRepository,
      SessionManager sessionManager, NostrRelayManager relayManager) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideP2PSyncEngine(pollRepository, residentRepository, voteRepository, manifestoRepository, accountRepository, sessionManager, relayManager));
  }
}
