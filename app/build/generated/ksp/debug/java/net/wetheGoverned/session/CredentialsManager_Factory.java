package net.wetheGoverned.session;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CredentialsManager_Factory implements Factory<CredentialsManager> {
  private final Provider<Context> contextProvider;

  public CredentialsManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CredentialsManager get() {
    return newInstance(contextProvider.get());
  }

  public static CredentialsManager_Factory create(Provider<Context> contextProvider) {
    return new CredentialsManager_Factory(contextProvider);
  }

  public static CredentialsManager newInstance(Context context) {
    return new CredentialsManager(context);
  }
}
