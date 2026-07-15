package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.session.CredentialsManager;
import net.wetheGoverned.session.SessionManager;

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
public final class CivicNetworkModule_ProvideSessionManagerFactory implements Factory<SessionManager> {
  private final Provider<CredentialsManager> storageProvider;

  public CivicNetworkModule_ProvideSessionManagerFactory(
      Provider<CredentialsManager> storageProvider) {
    this.storageProvider = storageProvider;
  }

  @Override
  public SessionManager get() {
    return provideSessionManager(storageProvider.get());
  }

  public static CivicNetworkModule_ProvideSessionManagerFactory create(
      Provider<CredentialsManager> storageProvider) {
    return new CivicNetworkModule_ProvideSessionManagerFactory(storageProvider);
  }

  public static SessionManager provideSessionManager(CredentialsManager storage) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideSessionManager(storage));
  }
}
