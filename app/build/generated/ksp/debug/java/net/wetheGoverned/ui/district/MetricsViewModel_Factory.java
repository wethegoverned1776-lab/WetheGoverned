package net.wetheGoverned.ui.district;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.DistrictRepository;
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
public final class MetricsViewModel_Factory implements Factory<MetricsViewModel> {
  private final Provider<DistrictRepository> districtRepositoryProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  public MetricsViewModel_Factory(Provider<DistrictRepository> districtRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    this.districtRepositoryProvider = districtRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
  }

  @Override
  public MetricsViewModel get() {
    return newInstance(districtRepositoryProvider.get(), residentRepositoryProvider.get());
  }

  public static MetricsViewModel_Factory create(
      Provider<DistrictRepository> districtRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    return new MetricsViewModel_Factory(districtRepositoryProvider, residentRepositoryProvider);
  }

  public static MetricsViewModel newInstance(DistrictRepository districtRepository,
      ResidentRepository residentRepository) {
    return new MetricsViewModel(districtRepository, residentRepository);
  }
}
