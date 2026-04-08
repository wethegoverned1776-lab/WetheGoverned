package net.wetheGoverned.ui.profile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
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
public final class ResidentProfileViewModel_Factory implements Factory<ResidentProfileViewModel> {
  private final Provider<ResidentRepository> residentRepositoryProvider;

  public ResidentProfileViewModel_Factory(Provider<ResidentRepository> residentRepositoryProvider) {
    this.residentRepositoryProvider = residentRepositoryProvider;
  }

  @Override
  public ResidentProfileViewModel get() {
    return newInstance(residentRepositoryProvider.get());
  }

  public static ResidentProfileViewModel_Factory create(
      Provider<ResidentRepository> residentRepositoryProvider) {
    return new ResidentProfileViewModel_Factory(residentRepositoryProvider);
  }

  public static ResidentProfileViewModel newInstance(ResidentRepository residentRepository) {
    return new ResidentProfileViewModel(residentRepository);
  }
}
