package net.wetheGoverned.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.local.dao.AccountDao;

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
public final class AndroidAccountRepository_Factory implements Factory<AndroidAccountRepository> {
  private final Provider<AccountDao> accountDaoProvider;

  public AndroidAccountRepository_Factory(Provider<AccountDao> accountDaoProvider) {
    this.accountDaoProvider = accountDaoProvider;
  }

  @Override
  public AndroidAccountRepository get() {
    return newInstance(accountDaoProvider.get());
  }

  public static AndroidAccountRepository_Factory create(Provider<AccountDao> accountDaoProvider) {
    return new AndroidAccountRepository_Factory(accountDaoProvider);
  }

  public static AndroidAccountRepository newInstance(AccountDao accountDao) {
    return new AndroidAccountRepository(accountDao);
  }
}
