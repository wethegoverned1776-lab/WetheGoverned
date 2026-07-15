package net.wetheGoverned.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import net.wetheGoverned.data.MeshDiscoveryManager;

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
public final class CivicNetworkModule_ProvideMeshDiscoveryManagerFactory implements Factory<MeshDiscoveryManager> {
  @Override
  public MeshDiscoveryManager get() {
    return provideMeshDiscoveryManager();
  }

  public static CivicNetworkModule_ProvideMeshDiscoveryManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MeshDiscoveryManager provideMeshDiscoveryManager() {
    return Preconditions.checkNotNullFromProvides(CivicNetworkModule.INSTANCE.provideMeshDiscoveryManager());
  }

  private static final class InstanceHolder {
    private static final CivicNetworkModule_ProvideMeshDiscoveryManagerFactory INSTANCE = new CivicNetworkModule_ProvideMeshDiscoveryManagerFactory();
  }
}
