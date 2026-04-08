package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.ManifestoRepository;
import net.wetheGoverned.repository.ResidentRepository;

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

  private final Provider<ResidentRepository> residentRepositoryProvider;

  public ManifestoViewModel_Factory(Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    this.manifestoRepositoryProvider = manifestoRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
  }

  @Override
  public ManifestoViewModel get() {
    return newInstance(manifestoRepositoryProvider.get(), residentRepositoryProvider.get());
  }

  public static ManifestoViewModel_Factory create(
      Provider<ManifestoRepository> manifestoRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    return new ManifestoViewModel_Factory(manifestoRepositoryProvider, residentRepositoryProvider);
  }

  public static ManifestoViewModel newInstance(ManifestoRepository manifestoRepository,
      ResidentRepository residentRepository) {
    return new ManifestoViewModel(manifestoRepository, residentRepository);
  }
}
