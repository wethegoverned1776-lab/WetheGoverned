package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.PollRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public HomeViewModel_Factory(Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(pollRepositoryProvider.get(), residentRepositoryProvider.get(), sessionManagerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new HomeViewModel_Factory(pollRepositoryProvider, residentRepositoryProvider, sessionManagerProvider);
  }

  public static HomeViewModel newInstance(PollRepository pollRepository,
      ResidentRepository residentRepository, SessionManager sessionManager) {
    return new HomeViewModel(pollRepository, residentRepository, sessionManager);
  }
}
