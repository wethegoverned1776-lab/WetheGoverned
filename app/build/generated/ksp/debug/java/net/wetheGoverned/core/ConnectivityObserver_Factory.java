package net.wetheGoverned.core;

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
public final class ConnectivityObserver_Factory implements Factory<ConnectivityObserver> {
  private final Provider<Context> contextProvider;

  public ConnectivityObserver_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConnectivityObserver get() {
    return newInstance(contextProvider.get());
  }

  public static ConnectivityObserver_Factory create(Provider<Context> contextProvider) {
    return new ConnectivityObserver_Factory(contextProvider);
  }

  public static ConnectivityObserver newInstance(Context context) {
    return new ConnectivityObserver(context);
  }
}
