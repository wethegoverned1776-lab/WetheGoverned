package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.ManifestoRepository;
import net.wetheGoverned.repository.PollRepository;
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
public final class ManifestoViewModel_Factory implements Factory<ManifestoViewModel> {
  private final Provider<ManifestoRepository> manifestoRepositoryProvider;

  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public ManifestoViewModel_Factory(Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<PollRepository> pollRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.manifestoRepositoryProvider = manifestoRepositoryProvider;
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public ManifestoViewModel get() {
    return newInstance(manifestoRepositoryProvider.get(), pollRepositoryProvider.get(), sessionManagerProvider.get());
  }

  public static ManifestoViewModel_Factory create(
      Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<PollRepository> pollRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new ManifestoViewModel_Factory(manifestoRepositoryProvider, pollRepositoryProvider, sessionManagerProvider);
  }

  public static ManifestoViewModel newInstance(ManifestoRepository manifestoRepository,
      PollRepository pollRepository, SessionManager sessionManager) {
    return new ManifestoViewModel(manifestoRepository, pollRepository, sessionManager);
  }
}
