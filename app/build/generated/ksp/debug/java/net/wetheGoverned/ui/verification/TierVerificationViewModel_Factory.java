package net.wetheGoverned.ui.verification;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.repository.AccountRepository;
import net.wetheGoverned.remote.api.CivicApi;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.session.SessionManager;

@ScopeMetadata
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
public final class TierVerificationViewModel_Factory implements Factory<TierVerificationViewModel> {
  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<CivicApi> civicApiProvider;

  public TierVerificationViewModel_Factory(Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<AccountRepository> accountRepositoryProvider, Provider<CivicApi> civicApiProvider) {
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.civicApiProvider = civicApiProvider;
  }

  @Override
  public TierVerificationViewModel get() {
    return newInstance(residentRepositoryProvider.get(), sessionManagerProvider.get(), accountRepositoryProvider.get(), civicApiProvider.get());
  }

  public static TierVerificationViewModel_Factory create(
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<AccountRepository> accountRepositoryProvider, Provider<CivicApi> civicApiProvider) {
    return new TierVerificationViewModel_Factory(residentRepositoryProvider, sessionManagerProvider, accountRepositoryProvider, civicApiProvider);
  }

  public static TierVerificationViewModel newInstance(ResidentRepository residentRepository,
      SessionManager sessionManager, AccountRepository accountRepository, CivicApi civicApi) {
    return new TierVerificationViewModel(residentRepository, sessionManager, accountRepository, civicApi);
  }
}
