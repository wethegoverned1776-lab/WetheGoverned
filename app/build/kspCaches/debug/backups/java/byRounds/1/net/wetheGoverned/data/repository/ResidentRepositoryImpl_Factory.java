package net.wetheGoverned.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.dao.ResidentProfileDao;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ResidentRepositoryImpl_Factory implements Factory<ResidentRepositoryImpl> {
  private final Provider<ResidentProfileDao> profileDaoProvider;

  public ResidentRepositoryImpl_Factory(Provider<ResidentProfileDao> profileDaoProvider) {
    this.profileDaoProvider = profileDaoProvider;
  }

  @Override
  public ResidentRepositoryImpl get() {
    return newInstance(profileDaoProvider.get());
  }

  public static ResidentRepositoryImpl_Factory create(
      Provider<ResidentProfileDao> profileDaoProvider) {
    return new ResidentRepositoryImpl_Factory(profileDaoProvider);
  }

  public static ResidentRepositoryImpl newInstance(ResidentProfileDao profileDao) {
    return new ResidentRepositoryImpl(profileDao);
  }
}
