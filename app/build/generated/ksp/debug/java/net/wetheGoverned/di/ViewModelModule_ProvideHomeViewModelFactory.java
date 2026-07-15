package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.NostrRelayManager;
import net.wetheGoverned.repository.PollRepository;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.HomeViewModel;

@ScopeMetadata("dagger.hilt.android.scopes.ViewModelScoped")
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
public final class ViewModelModule_ProvideHomeViewModelFactory implements Factory<HomeViewModel> {
  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<NostrRelayManager> relayManagerProvider;

  public ViewModelModule_ProvideHomeViewModelFactory(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<NostrRelayManager> relayManagerProvider) {
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.relayManagerProvider = relayManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return provideHomeViewModel(pollRepositoryProvider.get(), residentRepositoryProvider.get(), sessionManagerProvider.get(), relayManagerProvider.get());
  }

  public static ViewModelModule_ProvideHomeViewModelFactory create(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<NostrRelayManager> relayManagerProvider) {
    return new ViewModelModule_ProvideHomeViewModelFactory(pollRepositoryProvider, residentRepositoryProvider, sessionManagerProvider, relayManagerProvider);
  }

  public static HomeViewModel provideHomeViewModel(PollRepository pollRepository,
      ResidentRepository residentRepository, SessionManager sessionManager,
      NostrRelayManager relayManager) {
    return Preconditions.checkNotNullFromProvides(ViewModelModule.INSTANCE.provideHomeViewModel(pollRepository, residentRepository, sessionManager, relayManager));
  }
}
