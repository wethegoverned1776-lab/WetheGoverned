package net.wetheGoverned.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.dao.VerificationRequestDao;

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
public final class VerificationRequestRepositoryImpl_Factory implements Factory<VerificationRequestRepositoryImpl> {
  private final Provider<VerificationRequestDao> requestDaoProvider;

  public VerificationRequestRepositoryImpl_Factory(
      Provider<VerificationRequestDao> requestDaoProvider) {
    this.requestDaoProvider = requestDaoProvider;
  }

  @Override
  public VerificationRequestRepositoryImpl get() {
    return newInstance(requestDaoProvider.get());
  }

  public static VerificationRequestRepositoryImpl_Factory create(
      Provider<VerificationRequestDao> requestDaoProvider) {
    return new VerificationRequestRepositoryImpl_Factory(requestDaoProvider);
  }

  public static VerificationRequestRepositoryImpl newInstance(VerificationRequestDao requestDao) {
    return new VerificationRequestRepositoryImpl(requestDao);
  }
}
