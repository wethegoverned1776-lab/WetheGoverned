package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.PollRepository;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.PollViewModel;

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
public final class ViewModelModule_ProvidePollViewModelFactory implements Factory<PollViewModel> {
  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public ViewModelModule_ProvidePollViewModelFactory(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public PollViewModel get() {
    return providePollViewModel(pollRepositoryProvider.get(), sessionManagerProvider.get());
  }

  public static ViewModelModule_ProvidePollViewModelFactory create(
      Provider<PollRepository> pollRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new ViewModelModule_ProvidePollViewModelFactory(pollRepositoryProvider, sessionManagerProvider);
  }

  public static PollViewModel providePollViewModel(PollRepository pollRepository,
      SessionManager sessionManager) {
    return Preconditions.checkNotNullFromProvides(ViewModelModule.INSTANCE.providePollViewModel(pollRepository, sessionManager));
  }
}
