package net.wetheGoverned.ui.onboarding;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.remote.api.CivicApi;
import net.wetheGoverned.remote.backend.WtgBackendApi;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.location.LocationHelper;

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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<CivicApi> civicApiProvider;

  private final Provider<WtgBackendApi> backendApiProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<LocationHelper> locationHelperProvider;

  public OnboardingViewModel_Factory(Provider<CivicApi> civicApiProvider,
      Provider<WtgBackendApi> backendApiProvider, Provider<SessionManager> sessionManagerProvider,
      Provider<LocationHelper> locationHelperProvider) {
    this.civicApiProvider = civicApiProvider;
    this.backendApiProvider = backendApiProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.locationHelperProvider = locationHelperProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(civicApiProvider.get(), backendApiProvider.get(), sessionManagerProvider.get(), locationHelperProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<CivicApi> civicApiProvider,
      Provider<WtgBackendApi> backendApiProvider, Provider<SessionManager> sessionManagerProvider,
      Provider<LocationHelper> locationHelperProvider) {
    return new OnboardingViewModel_Factory(civicApiProvider, backendApiProvider, sessionManagerProvider, locationHelperProvider);
  }

  public static OnboardingViewModel newInstance(CivicApi civicApi, WtgBackendApi backendApi,
      SessionManager sessionManager, LocationHelper locationHelper) {
    return new OnboardingViewModel(civicApi, backendApi, sessionManager, locationHelper);
  }
}
