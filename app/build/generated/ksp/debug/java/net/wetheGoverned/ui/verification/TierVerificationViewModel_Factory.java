package net.wetheGoverned.ui.verification;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.remote.backend.WtgBackendApi;
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

  private final Provider<WtgBackendApi> backendApiProvider;

  public TierVerificationViewModel_Factory(Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider, Provider<WtgBackendApi> backendApiProvider) {
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.backendApiProvider = backendApiProvider;
  }

  @Override
  public TierVerificationViewModel get() {
    return newInstance(residentRepositoryProvider.get(), sessionManagerProvider.get(), backendApiProvider.get());
  }

  public static TierVerificationViewModel_Factory create(
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider, Provider<WtgBackendApi> backendApiProvider) {
    return new TierVerificationViewModel_Factory(residentRepositoryProvider, sessionManagerProvider, backendApiProvider);
  }

  public static TierVerificationViewModel newInstance(ResidentRepository residentRepository,
      SessionManager sessionManager, WtgBackendApi backendApi) {
    return new TierVerificationViewModel(residentRepository, sessionManager, backendApi);
  }
}
