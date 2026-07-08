package net.wetheGoverned;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import io.ktor.client.HttpClient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.wetheGoverned.data.NostrRelayManager;
import net.wetheGoverned.data.P2PService;
import net.wetheGoverned.data.P2PService_MembersInjector;
import net.wetheGoverned.data.P2PSyncEngine;
import net.wetheGoverned.data.repository.AndroidAccountRepository;
import net.wetheGoverned.data.repository.CommunityRepositoryImpl;
import net.wetheGoverned.data.repository.DistrictRepositoryImpl;
import net.wetheGoverned.data.repository.ManifestoRepositoryImpl;
import net.wetheGoverned.data.repository.PollRepositoryImpl;
import net.wetheGoverned.data.repository.ResidentRepositoryImpl;
import net.wetheGoverned.data.repository.ScorecardRepositoryImpl;
import net.wetheGoverned.data.repository.VoteRepositoryImpl;
import net.wetheGoverned.di.CivicNetworkModule_ProvideCivicApiFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideHttpClientFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideLocationHelperFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideNostrRelayManagerFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideP2PSyncEngineFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideRelayUrlsFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideSessionManagerFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideWtgBackendApiFactory;
import net.wetheGoverned.remote.api.CivicApi;
import net.wetheGoverned.remote.api.WtgBackendApi;
import net.wetheGoverned.repository.AccountRepository;
import net.wetheGoverned.repository.CommunityRepository;
import net.wetheGoverned.repository.DistrictRepository;
import net.wetheGoverned.repository.ManifestoRepository;
import net.wetheGoverned.repository.PollRepository;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.repository.ScorecardRepository;
import net.wetheGoverned.session.CredentialsManager;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.location.LocationHelper;

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
public final class DaggerWeTheGovernedApplication_HiltComponents_SingletonC {
  private DaggerWeTheGovernedApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public WeTheGovernedApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements WeTheGovernedApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements WeTheGovernedApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements WeTheGovernedApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements WeTheGovernedApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements WeTheGovernedApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements WeTheGovernedApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements WeTheGovernedApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public WeTheGovernedApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends WeTheGovernedApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends WeTheGovernedApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends WeTheGovernedApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends WeTheGovernedApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(Collections.<Class<?>, Boolean>emptyMap(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return Collections.<Class<?>, Boolean>emptyMap();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }
  }

  private static final class ViewModelCImpl extends WeTheGovernedApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public Map<Class<?>, Provider<ViewModel>> getHiltViewModelMap() {
      return Collections.<Class<?>, Provider<ViewModel>>emptyMap();
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }
  }

  private static final class ActivityRetainedCImpl extends WeTheGovernedApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private dagger.internal.Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends WeTheGovernedApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectP2PService(P2PService p2PService) {
      injectP2PService2(p2PService);
    }

    @CanIgnoreReturnValue
    private P2PService injectP2PService2(P2PService instance) {
      P2PService_MembersInjector.injectP2pSyncEngine(instance, singletonCImpl.provideP2PSyncEngineProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends WeTheGovernedApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private dagger.internal.Provider<PollRepositoryImpl> pollRepositoryImplProvider;

    private dagger.internal.Provider<ResidentRepositoryImpl> residentRepositoryImplProvider;

    private dagger.internal.Provider<VoteRepositoryImpl> voteRepositoryImplProvider;

    private dagger.internal.Provider<ManifestoRepositoryImpl> manifestoRepositoryImplProvider;

    private dagger.internal.Provider<AndroidAccountRepository> androidAccountRepositoryProvider;

    private dagger.internal.Provider<CredentialsManager> credentialsManagerProvider;

    private dagger.internal.Provider<SessionManager> provideSessionManagerProvider;

    private dagger.internal.Provider<List<String>> provideRelayUrlsProvider;

    private dagger.internal.Provider<NostrRelayManager> provideNostrRelayManagerProvider;

    private dagger.internal.Provider<P2PSyncEngine> provideP2PSyncEngineProvider;

    private dagger.internal.Provider<ScorecardRepositoryImpl> scorecardRepositoryImplProvider;

    private dagger.internal.Provider<DistrictRepositoryImpl> districtRepositoryImplProvider;

    private dagger.internal.Provider<CommunityRepositoryImpl> communityRepositoryImplProvider;

    private dagger.internal.Provider<CivicApi> provideCivicApiProvider;

    private dagger.internal.Provider<HttpClient> provideHttpClientProvider;

    private dagger.internal.Provider<WtgBackendApi> provideWtgBackendApiProvider;

    private dagger.internal.Provider<LocationHelper> provideLocationHelperProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.pollRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PollRepositoryImpl>(singletonCImpl, 1));
      this.residentRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ResidentRepositoryImpl>(singletonCImpl, 2));
      this.voteRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<VoteRepositoryImpl>(singletonCImpl, 3));
      this.manifestoRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ManifestoRepositoryImpl>(singletonCImpl, 4));
      this.androidAccountRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AndroidAccountRepository>(singletonCImpl, 5));
      this.credentialsManagerProvider = DoubleCheck.provider(new SwitchingProvider<CredentialsManager>(singletonCImpl, 7));
      this.provideSessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<SessionManager>(singletonCImpl, 6));
      this.provideRelayUrlsProvider = DoubleCheck.provider(new SwitchingProvider<List<String>>(singletonCImpl, 9));
      this.provideNostrRelayManagerProvider = DoubleCheck.provider(new SwitchingProvider<NostrRelayManager>(singletonCImpl, 8));
      this.provideP2PSyncEngineProvider = DoubleCheck.provider(new SwitchingProvider<P2PSyncEngine>(singletonCImpl, 0));
      this.scorecardRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ScorecardRepositoryImpl>(singletonCImpl, 10));
      this.districtRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DistrictRepositoryImpl>(singletonCImpl, 11));
      this.communityRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CommunityRepositoryImpl>(singletonCImpl, 12));
      this.provideCivicApiProvider = DoubleCheck.provider(new SwitchingProvider<CivicApi>(singletonCImpl, 13));
      this.provideHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<HttpClient>(singletonCImpl, 15));
      this.provideWtgBackendApiProvider = DoubleCheck.provider(new SwitchingProvider<WtgBackendApi>(singletonCImpl, 14));
      this.provideLocationHelperProvider = DoubleCheck.provider(new SwitchingProvider<LocationHelper>(singletonCImpl, 16));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectWeTheGovernedApplication(WeTheGovernedApplication weTheGovernedApplication) {
      injectWeTheGovernedApplication2(weTheGovernedApplication);
    }

    @Override
    public PollRepository pollRepository() {
      return pollRepositoryImplProvider.get();
    }

    @Override
    public AccountRepository accountRepository() {
      return androidAccountRepositoryProvider.get();
    }

    @Override
    public ResidentRepository residentRepository() {
      return residentRepositoryImplProvider.get();
    }

    @Override
    public ManifestoRepository manifestoRepository() {
      return manifestoRepositoryImplProvider.get();
    }

    @Override
    public ScorecardRepository scorecardRepository() {
      return scorecardRepositoryImplProvider.get();
    }

    @Override
    public DistrictRepository districtRepository() {
      return districtRepositoryImplProvider.get();
    }

    @Override
    public CommunityRepository communityRepository() {
      return communityRepositoryImplProvider.get();
    }

    @Override
    public SessionManager sessionManager() {
      return provideSessionManagerProvider.get();
    }

    @Override
    public CivicApi civicApi() {
      return provideCivicApiProvider.get();
    }

    @Override
    public WtgBackendApi wtgBackendApi() {
      return provideWtgBackendApiProvider.get();
    }

    @Override
    public LocationHelper locationHelper() {
      return provideLocationHelperProvider.get();
    }

    @Override
    public NostrRelayManager relayManager() {
      return provideNostrRelayManagerProvider.get();
    }

    @CanIgnoreReturnValue
    private WeTheGovernedApplication injectWeTheGovernedApplication2(
        WeTheGovernedApplication instance) {
      WeTheGovernedApplication_MembersInjector.injectP2pSyncEngine(instance, provideP2PSyncEngineProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // net.wetheGoverned.data.P2PSyncEngine 
          return (T) CivicNetworkModule_ProvideP2PSyncEngineFactory.provideP2PSyncEngine(singletonCImpl.pollRepositoryImplProvider.get(), singletonCImpl.residentRepositoryImplProvider.get(), singletonCImpl.voteRepositoryImplProvider.get(), singletonCImpl.manifestoRepositoryImplProvider.get(), singletonCImpl.androidAccountRepositoryProvider.get(), singletonCImpl.provideSessionManagerProvider.get(), singletonCImpl.provideNostrRelayManagerProvider.get());

          case 1: // net.wetheGoverned.data.repository.PollRepositoryImpl 
          return (T) new PollRepositoryImpl();

          case 2: // net.wetheGoverned.data.repository.ResidentRepositoryImpl 
          return (T) new ResidentRepositoryImpl();

          case 3: // net.wetheGoverned.data.repository.VoteRepositoryImpl 
          return (T) new VoteRepositoryImpl();

          case 4: // net.wetheGoverned.data.repository.ManifestoRepositoryImpl 
          return (T) new ManifestoRepositoryImpl();

          case 5: // net.wetheGoverned.data.repository.AndroidAccountRepository 
          return (T) new AndroidAccountRepository();

          case 6: // net.wetheGoverned.session.SessionManager 
          return (T) CivicNetworkModule_ProvideSessionManagerFactory.provideSessionManager(singletonCImpl.credentialsManagerProvider.get());

          case 7: // net.wetheGoverned.session.CredentialsManager 
          return (T) new CredentialsManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // net.wetheGoverned.data.NostrRelayManager 
          return (T) CivicNetworkModule_ProvideNostrRelayManagerFactory.provideNostrRelayManager(singletonCImpl.provideRelayUrlsProvider.get());

          case 9: // java.util.List<java.lang.String> 
          return (T) CivicNetworkModule_ProvideRelayUrlsFactory.provideRelayUrls();

          case 10: // net.wetheGoverned.data.repository.ScorecardRepositoryImpl 
          return (T) new ScorecardRepositoryImpl();

          case 11: // net.wetheGoverned.data.repository.DistrictRepositoryImpl 
          return (T) new DistrictRepositoryImpl();

          case 12: // net.wetheGoverned.data.repository.CommunityRepositoryImpl 
          return (T) new CommunityRepositoryImpl();

          case 13: // net.wetheGoverned.remote.api.CivicApi 
          return (T) CivicNetworkModule_ProvideCivicApiFactory.provideCivicApi();

          case 14: // net.wetheGoverned.remote.api.WtgBackendApi 
          return (T) CivicNetworkModule_ProvideWtgBackendApiFactory.provideWtgBackendApi(singletonCImpl.provideHttpClientProvider.get());

          case 15: // io.ktor.client.HttpClient 
          return (T) CivicNetworkModule_ProvideHttpClientFactory.provideHttpClient();

          case 16: // net.wetheGoverned.ui.location.LocationHelper 
          return (T) CivicNetworkModule_ProvideLocationHelperFactory.provideLocationHelper(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
