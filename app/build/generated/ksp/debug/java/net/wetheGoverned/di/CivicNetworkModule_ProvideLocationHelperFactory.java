package net.wetheGoverned.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.ui.location.LocationHelper;

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
public final class CivicNetworkModule_ProvideLocationHelperFactory implements Factory<LocationHelper> {
  private final Provider<Context> contextProvider;

  public CivicNetworkModule_ProvideLocationHelperFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocationHelper get() {
    return provideLocationHelper(contextProvider.get());
  }

  public static CivicNetworkModule_ProvideLocationHelperFactory create(
      Provider<Context> contextProvider) {
    return new CivicNetworkModule_ProvideLocationHelperFactory(contextProvider);
  }

  public static LocationHelper provideLocationHelper(Context context) {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideLocationHelper(context));
  }
}
