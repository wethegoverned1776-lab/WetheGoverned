package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.AccountRepository;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.repository.VerificationRequestRepository;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.ResidentProfileViewModel;

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
public final class ViewModelModule_ProvideResidentProfileViewModelFactory implements Factory<ResidentProfileViewModel> {
  private final Provider<ResidentRepository> residentRepositoryProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<VerificationRequestRepository> requestRepositoryProvider;

  public ViewModelModule_ProvideResidentProfileViewModelFactory(
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<VerificationRequestRepository> requestRepositoryProvider) {
    this.residentRepositoryProvider = residentRepositoryProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.requestRepositoryProvider = requestRepositoryProvider;
  }

  @Override
  public ResidentProfileViewModel get() {
    return provideResidentProfileViewModel(residentRepositoryProvider.get(), accountRepositoryProvider.get(), sessionManagerProvider.get(), requestRepositoryProvider.get());
  }

  public static ViewModelModule_ProvideResidentProfileViewModelFactory create(
      Provider<ResidentRepository> residentRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<VerificationRequestRepository> requestRepositoryProvider) {
    return new ViewModelModule_ProvideResidentProfileViewModelFactory(residentRepositoryProvider, accountRepositoryProvider, sessionManagerProvider, requestRepositoryProvider);
  }

  public static ResidentProfileViewModel provideResidentProfileViewModel(
      ResidentRepository residentRepository, AccountRepository accountRepository,
      SessionManager sessionManager, VerificationRequestRepository requestRepository) {
    return Preconditions.checkNotNullFromProvides(ViewModelModule.INSTANCE.provideResidentProfileViewModel(residentRepository, accountRepository, sessionManager, requestRepository));
  }
}
