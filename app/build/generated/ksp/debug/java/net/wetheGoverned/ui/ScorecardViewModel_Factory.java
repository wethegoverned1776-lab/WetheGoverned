package net.wetheGoverned.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.repository.ScorecardRepository;

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
public final class ScorecardViewModel_Factory implements Factory<ScorecardViewModel> {
  private final Provider<ScorecardRepository> scorecardRepositoryProvider;

  public ScorecardViewModel_Factory(Provider<ScorecardRepository> scorecardRepositoryProvider) {
    this.scorecardRepositoryProvider = scorecardRepositoryProvider;
  }

  @Override
  public ScorecardViewModel get() {
    return newInstance(scorecardRepositoryProvider.get());
  }

  public static ScorecardViewModel_Factory create(
      Provider<ScorecardRepository> scorecardRepositoryProvider) {
    return new ScorecardViewModel_Factory(scorecardRepositoryProvider);
  }

  public static ScorecardViewModel newInstance(ScorecardRepository scorecardRepository) {
    return new ScorecardViewModel(scorecardRepository);
  }
}
