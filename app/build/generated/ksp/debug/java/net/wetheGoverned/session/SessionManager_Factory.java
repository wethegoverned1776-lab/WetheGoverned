package net.wetheGoverned.session;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class SessionManager_Factory implements Factory<SessionManager> {
  @Override
  public SessionManager get() {
    return newInstance();
  }

  public static SessionManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SessionManager newInstance() {
    return new SessionManager();
  }

  private static final class InstanceHolder {
    private static final SessionManager_Factory INSTANCE = new SessionManager_Factory();
  }
}
