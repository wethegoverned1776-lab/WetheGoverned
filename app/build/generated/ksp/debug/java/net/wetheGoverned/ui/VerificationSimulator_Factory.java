package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.remote.api.CivicApi;
import net.wetheGoverned.repository.AccountRepository;
import net.wetheGoverned.repository.ResidentRepository;
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
public final class VerificationSimulator_Factory implements Factory<VerificationSimulator> {
  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<CivicApi> civicApiProvider;

  public VerificationSimulator_Factory(Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<CivicApi> civicApiProvider) {
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.civicApiProvider = civicApiProvider;
  }

  @Override
  public VerificationSimulator get() {
    return newInstance(accountRepositoryProvider.get(), sessionManagerProvider.get(), residentRepositoryProvider.get(), civicApiProvider.get());
  }

  public static VerificationSimulator_Factory create(
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<CivicApi> civicApiProvider) {
    return new VerificationSimulator_Factory(accountRepositoryProvider, sessionManagerProvider, residentRepositoryProvider, civicApiProvider);
  }

  public static VerificationSimulator newInstance(AccountRepository accountRepository,
      SessionManager sessionManager, ResidentRepository residentRepository, CivicApi civicApi) {
    return new VerificationSimulator(accountRepository, sessionManager, residentRepository, civicApi);
  }
}
