package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.PollRepository;
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
public final class PollDetailViewModel_Factory implements Factory<PollDetailViewModel> {
  private final Provider<PollRepository> pollRepositoryProvider;

  private final Provider<ResidentRepository> residentRepositoryProvider;

  public PollDetailViewModel_Factory(Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    this.pollRepositoryProvider = pollRepositoryProvider;
    this.residentRepositoryProvider = residentRepositoryProvider;
  }

  @Override
  public PollDetailViewModel get() {
    return newInstance(pollRepositoryProvider.get(), residentRepositoryProvider.get());
  }

  public static PollDetailViewModel_Factory create(Provider<PollRepository> pollRepositoryProvider,
      Provider<ResidentRepository> residentRepositoryProvider) {
    return new PollDetailViewModel_Factory(pollRepositoryProvider, residentRepositoryProvider);
  }

  public static PollDetailViewModel newInstance(PollRepository pollRepository,
      ResidentRepository residentRepository) {
    return new PollDetailViewModel(pollRepository, residentRepository);
  }
}
