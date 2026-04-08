package net.wetheGoverned;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
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
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import net.wetheGoverned.core.DefaultDispatcherProvider;
import net.wetheGoverned.core.DispatcherProvider;
import net.wetheGoverned.core.WsCivicPublisherWithQueue;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideDatabaseFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideDistrictDaoFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideManifestoDaoFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideMetricDaoFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvidePollDaoFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideProfileDaoFactory;
import net.wetheGoverned.di.CivicDatabaseModule_ProvideScorecardDaoFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideCivicApiFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideRelayUrlFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideWsCivicPublisherFactory;
import net.wetheGoverned.di.CivicNetworkModule_ProvideWtgBackendApiFactory;
import net.wetheGoverned.local.CivicDatabase;
import net.wetheGoverned.local.dao.DistrictDao;
import net.wetheGoverned.local.dao.ManifestoDao;
import net.wetheGoverned.local.dao.MetricDao;
import net.wetheGoverned.local.dao.PollDao;
import net.wetheGoverned.local.dao.ResidentProfileDao;
import net.wetheGoverned.local.dao.ScorecardDao;
import net.wetheGoverned.remote.api.CivicApi;
import net.wetheGoverned.remote.backend.WtgBackendApi;
import net.wetheGoverned.repository.DistrictRepository;
import net.wetheGoverned.repository.DistrictRepositoryImpl;
import net.wetheGoverned.repository.ManifestoRepository;
import net.wetheGoverned.repository.ManifestoRepositoryImpl;
import net.wetheGoverned.repository.PollRepository;
import net.wetheGoverned.repository.PollRepositoryImpl;
import net.wetheGoverned.repository.ResidentRepository;
import net.wetheGoverned.repository.ResidentRepositoryImpl;
import net.wetheGoverned.repository.ScorecardRepository;
import net.wetheGoverned.repository.ScorecardRepositoryImpl;
import net.wetheGoverned.session.PendingEventQueue;
import net.wetheGoverned.session.SessionManager;
import net.wetheGoverned.ui.HomeViewModel;
import net.wetheGoverned.ui.HomeViewModel_HiltModules;
import net.wetheGoverned.ui.ManifestoViewModel;
import net.wetheGoverned.ui.ManifestoViewModel_HiltModules;
import net.wetheGoverned.ui.PollDetailViewModel;
import net.wetheGoverned.ui.PollDetailViewModel_HiltModules;
import net.wetheGoverned.ui.ScorecardViewModel;
import net.wetheGoverned.ui.ScorecardViewModel_HiltModules;
import net.wetheGoverned.ui.district.MetricsViewModel;
import net.wetheGoverned.ui.district.MetricsViewModel_HiltModules;
import net.wetheGoverned.ui.location.LocationHelper;
import net.wetheGoverned.ui.onboarding.OnboardingViewModel;
import net.wetheGoverned.ui.onboarding.OnboardingViewModel_HiltModules;
import net.wetheGoverned.ui.profile.ResidentProfileViewModel;
import net.wetheGoverned.ui.profile.ResidentProfileViewModel_HiltModules;
import net.wetheGoverned.ui.verification.TierVerificationViewModel;
import net.wetheGoverned.ui.verification.TierVerificationViewModel_HiltModules;

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
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.net_wetheGoverned_ui_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_ManifestoViewModel, ManifestoViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_district_MetricsViewModel, MetricsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_PollDetailViewModel, PollDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_profile_ResidentProfileViewModel, ResidentProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_ScorecardViewModel, ScorecardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_wetheGoverned_ui_verification_TierVerificationViewModel, TierVerificationViewModel_HiltModules.KeyModule.provide()).build());
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

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String net_wetheGoverned_ui_verification_TierVerificationViewModel = "net.wetheGoverned.ui.verification.TierVerificationViewModel";

      static String net_wetheGoverned_ui_ScorecardViewModel = "net.wetheGoverned.ui.ScorecardViewModel";

      static String net_wetheGoverned_ui_HomeViewModel = "net.wetheGoverned.ui.HomeViewModel";

      static String net_wetheGoverned_ui_profile_ResidentProfileViewModel = "net.wetheGoverned.ui.profile.ResidentProfileViewModel";

      static String net_wetheGoverned_ui_PollDetailViewModel = "net.wetheGoverned.ui.PollDetailViewModel";

      static String net_wetheGoverned_ui_ManifestoViewModel = "net.wetheGoverned.ui.ManifestoViewModel";

      static String net_wetheGoverned_ui_district_MetricsViewModel = "net.wetheGoverned.ui.district.MetricsViewModel";

      static String net_wetheGoverned_ui_onboarding_OnboardingViewModel = "net.wetheGoverned.ui.onboarding.OnboardingViewModel";

      @KeepFieldType
      TierVerificationViewModel net_wetheGoverned_ui_verification_TierVerificationViewModel2;

      @KeepFieldType
      ScorecardViewModel net_wetheGoverned_ui_ScorecardViewModel2;

      @KeepFieldType
      HomeViewModel net_wetheGoverned_ui_HomeViewModel2;

      @KeepFieldType
      ResidentProfileViewModel net_wetheGoverned_ui_profile_ResidentProfileViewModel2;

      @KeepFieldType
      PollDetailViewModel net_wetheGoverned_ui_PollDetailViewModel2;

      @KeepFieldType
      ManifestoViewModel net_wetheGoverned_ui_ManifestoViewModel2;

      @KeepFieldType
      MetricsViewModel net_wetheGoverned_ui_district_MetricsViewModel2;

      @KeepFieldType
      OnboardingViewModel net_wetheGoverned_ui_onboarding_OnboardingViewModel2;
    }
  }

  private static final class ViewModelCImpl extends WeTheGovernedApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<ManifestoViewModel> manifestoViewModelProvider;

    private Provider<MetricsViewModel> metricsViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PollDetailViewModel> pollDetailViewModelProvider;

    private Provider<ResidentProfileViewModel> residentProfileViewModelProvider;

    private Provider<ScorecardViewModel> scorecardViewModelProvider;

    private Provider<TierVerificationViewModel> tierVerificationViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.manifestoViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.metricsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.pollDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.residentProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.scorecardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.tierVerificationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.net_wetheGoverned_ui_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_ManifestoViewModel, ((Provider) manifestoViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_district_MetricsViewModel, ((Provider) metricsViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_PollDetailViewModel, ((Provider) pollDetailViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_profile_ResidentProfileViewModel, ((Provider) residentProfileViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_ScorecardViewModel, ((Provider) scorecardViewModelProvider)).put(LazyClassKeyProvider.net_wetheGoverned_ui_verification_TierVerificationViewModel, ((Provider) tierVerificationViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String net_wetheGoverned_ui_ManifestoViewModel = "net.wetheGoverned.ui.ManifestoViewModel";

      static String net_wetheGoverned_ui_district_MetricsViewModel = "net.wetheGoverned.ui.district.MetricsViewModel";

      static String net_wetheGoverned_ui_verification_TierVerificationViewModel = "net.wetheGoverned.ui.verification.TierVerificationViewModel";

      static String net_wetheGoverned_ui_ScorecardViewModel = "net.wetheGoverned.ui.ScorecardViewModel";

      static String net_wetheGoverned_ui_HomeViewModel = "net.wetheGoverned.ui.HomeViewModel";

      static String net_wetheGoverned_ui_onboarding_OnboardingViewModel = "net.wetheGoverned.ui.onboarding.OnboardingViewModel";

      static String net_wetheGoverned_ui_PollDetailViewModel = "net.wetheGoverned.ui.PollDetailViewModel";

      static String net_wetheGoverned_ui_profile_ResidentProfileViewModel = "net.wetheGoverned.ui.profile.ResidentProfileViewModel";

      @KeepFieldType
      ManifestoViewModel net_wetheGoverned_ui_ManifestoViewModel2;

      @KeepFieldType
      MetricsViewModel net_wetheGoverned_ui_district_MetricsViewModel2;

      @KeepFieldType
      TierVerificationViewModel net_wetheGoverned_ui_verification_TierVerificationViewModel2;

      @KeepFieldType
      ScorecardViewModel net_wetheGoverned_ui_ScorecardViewModel2;

      @KeepFieldType
      HomeViewModel net_wetheGoverned_ui_HomeViewModel2;

      @KeepFieldType
      OnboardingViewModel net_wetheGoverned_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      PollDetailViewModel net_wetheGoverned_ui_PollDetailViewModel2;

      @KeepFieldType
      ResidentProfileViewModel net_wetheGoverned_ui_profile_ResidentProfileViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // net.wetheGoverned.ui.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.bindPollRepositoryProvider.get(), singletonCImpl.bindResidentRepositoryProvider.get(), singletonCImpl.sessionManagerProvider.get());

          case 1: // net.wetheGoverned.ui.ManifestoViewModel 
          return (T) new ManifestoViewModel(singletonCImpl.bindManifestoRepositoryProvider.get(), singletonCImpl.bindResidentRepositoryProvider.get());

          case 2: // net.wetheGoverned.ui.district.MetricsViewModel 
          return (T) new MetricsViewModel(singletonCImpl.bindDistrictRepositoryProvider.get(), singletonCImpl.bindResidentRepositoryProvider.get());

          case 3: // net.wetheGoverned.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.provideWtgBackendApiProvider.get(), singletonCImpl.sessionManagerProvider.get(), singletonCImpl.locationHelperProvider.get());

          case 4: // net.wetheGoverned.ui.PollDetailViewModel 
          return (T) new PollDetailViewModel(singletonCImpl.bindPollRepositoryProvider.get(), singletonCImpl.bindResidentRepositoryProvider.get());

          case 5: // net.wetheGoverned.ui.profile.ResidentProfileViewModel 
          return (T) new ResidentProfileViewModel(singletonCImpl.bindResidentRepositoryProvider.get());

          case 6: // net.wetheGoverned.ui.ScorecardViewModel 
          return (T) new ScorecardViewModel(singletonCImpl.bindScorecardRepositoryProvider.get());

          case 7: // net.wetheGoverned.ui.verification.TierVerificationViewModel 
          return (T) new TierVerificationViewModel(singletonCImpl.bindResidentRepositoryProvider.get(), singletonCImpl.sessionManagerProvider.get(), singletonCImpl.provideWtgBackendApiProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends WeTheGovernedApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

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

    private static final class SwitchingProvider<T> implements Provider<T> {
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
  }

  private static final class SingletonCImpl extends WeTheGovernedApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<CivicDatabase> provideDatabaseProvider;

    private Provider<PollDao> providePollDaoProvider;

    private Provider<String> provideRelayUrlProvider;

    private Provider<CivicApi> provideCivicApiProvider;

    private Provider<SessionManager> sessionManagerProvider;

    private Provider<DefaultDispatcherProvider> defaultDispatcherProvider;

    private Provider<DispatcherProvider> bindDispatcherProvider;

    private Provider<PendingEventQueue> pendingEventQueueProvider;

    private Provider<WsCivicPublisherWithQueue> provideWsCivicPublisherProvider;

    private Provider<PollRepositoryImpl> pollRepositoryImplProvider;

    private Provider<PollRepository> bindPollRepositoryProvider;

    private Provider<ResidentProfileDao> provideProfileDaoProvider;

    private Provider<ResidentRepositoryImpl> residentRepositoryImplProvider;

    private Provider<ResidentRepository> bindResidentRepositoryProvider;

    private Provider<ManifestoDao> provideManifestoDaoProvider;

    private Provider<ManifestoRepositoryImpl> manifestoRepositoryImplProvider;

    private Provider<ManifestoRepository> bindManifestoRepositoryProvider;

    private Provider<DistrictDao> provideDistrictDaoProvider;

    private Provider<MetricDao> provideMetricDaoProvider;

    private Provider<DistrictRepositoryImpl> districtRepositoryImplProvider;

    private Provider<DistrictRepository> bindDistrictRepositoryProvider;

    private Provider<WtgBackendApi> provideWtgBackendApiProvider;

    private Provider<LocationHelper> locationHelperProvider;

    private Provider<ScorecardDao> provideScorecardDaoProvider;

    private Provider<ScorecardRepositoryImpl> scorecardRepositoryImplProvider;

    private Provider<ScorecardRepository> bindScorecardRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<CivicDatabase>(singletonCImpl, 2));
      this.providePollDaoProvider = DoubleCheck.provider(new SwitchingProvider<PollDao>(singletonCImpl, 1));
      this.provideRelayUrlProvider = DoubleCheck.provider(new SwitchingProvider<String>(singletonCImpl, 4));
      this.provideCivicApiProvider = DoubleCheck.provider(new SwitchingProvider<CivicApi>(singletonCImpl, 3));
      this.sessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<SessionManager>(singletonCImpl, 6));
      this.defaultDispatcherProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.bindDispatcherProvider = DoubleCheck.provider((Provider) defaultDispatcherProvider);
      this.pendingEventQueueProvider = DoubleCheck.provider(new SwitchingProvider<PendingEventQueue>(singletonCImpl, 8));
      this.provideWsCivicPublisherProvider = DoubleCheck.provider(new SwitchingProvider<WsCivicPublisherWithQueue>(singletonCImpl, 5));
      this.pollRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 0);
      this.bindPollRepositoryProvider = DoubleCheck.provider((Provider) pollRepositoryImplProvider);
      this.provideProfileDaoProvider = DoubleCheck.provider(new SwitchingProvider<ResidentProfileDao>(singletonCImpl, 10));
      this.residentRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindResidentRepositoryProvider = DoubleCheck.provider((Provider) residentRepositoryImplProvider);
      this.provideManifestoDaoProvider = DoubleCheck.provider(new SwitchingProvider<ManifestoDao>(singletonCImpl, 12));
      this.manifestoRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.bindManifestoRepositoryProvider = DoubleCheck.provider((Provider) manifestoRepositoryImplProvider);
      this.provideDistrictDaoProvider = DoubleCheck.provider(new SwitchingProvider<DistrictDao>(singletonCImpl, 14));
      this.provideMetricDaoProvider = DoubleCheck.provider(new SwitchingProvider<MetricDao>(singletonCImpl, 15));
      this.districtRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindDistrictRepositoryProvider = DoubleCheck.provider((Provider) districtRepositoryImplProvider);
      this.provideWtgBackendApiProvider = DoubleCheck.provider(new SwitchingProvider<WtgBackendApi>(singletonCImpl, 16));
      this.locationHelperProvider = DoubleCheck.provider(new SwitchingProvider<LocationHelper>(singletonCImpl, 17));
      this.provideScorecardDaoProvider = DoubleCheck.provider(new SwitchingProvider<ScorecardDao>(singletonCImpl, 19));
      this.scorecardRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 18);
      this.bindScorecardRepositoryProvider = DoubleCheck.provider((Provider) scorecardRepositoryImplProvider);
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
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
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
          case 0: // net.wetheGoverned.repository.PollRepositoryImpl 
          return (T) new PollRepositoryImpl(singletonCImpl.providePollDaoProvider.get(), singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.provideWsCivicPublisherProvider.get(), singletonCImpl.bindDispatcherProvider.get());

          case 1: // net.wetheGoverned.local.dao.PollDao 
          return (T) CivicDatabaseModule_ProvidePollDaoFactory.providePollDao(singletonCImpl.provideDatabaseProvider.get());

          case 2: // net.wetheGoverned.local.CivicDatabase 
          return (T) CivicDatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // net.wetheGoverned.remote.api.CivicApi 
          return (T) CivicNetworkModule_ProvideCivicApiFactory.provideCivicApi(singletonCImpl.provideRelayUrlProvider.get());

          case 4: // java.lang.String 
          return (T) CivicNetworkModule_ProvideRelayUrlFactory.provideRelayUrl();

          case 5: // net.wetheGoverned.core.WsCivicPublisherWithQueue 
          return (T) CivicNetworkModule_ProvideWsCivicPublisherFactory.provideWsCivicPublisher(singletonCImpl.provideRelayUrlProvider.get(), singletonCImpl.sessionManagerProvider.get(), singletonCImpl.bindDispatcherProvider.get(), singletonCImpl.pendingEventQueueProvider.get());

          case 6: // net.wetheGoverned.session.SessionManager 
          return (T) new SessionManager();

          case 7: // net.wetheGoverned.core.DefaultDispatcherProvider 
          return (T) new DefaultDispatcherProvider();

          case 8: // net.wetheGoverned.session.PendingEventQueue 
          return (T) new PendingEventQueue();

          case 9: // net.wetheGoverned.repository.ResidentRepositoryImpl 
          return (T) new ResidentRepositoryImpl(singletonCImpl.provideProfileDaoProvider.get(), singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.bindDispatcherProvider.get());

          case 10: // net.wetheGoverned.local.dao.ResidentProfileDao 
          return (T) CivicDatabaseModule_ProvideProfileDaoFactory.provideProfileDao(singletonCImpl.provideDatabaseProvider.get());

          case 11: // net.wetheGoverned.repository.ManifestoRepositoryImpl 
          return (T) new ManifestoRepositoryImpl(singletonCImpl.provideManifestoDaoProvider.get(), singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.provideWsCivicPublisherProvider.get(), singletonCImpl.bindDispatcherProvider.get());

          case 12: // net.wetheGoverned.local.dao.ManifestoDao 
          return (T) CivicDatabaseModule_ProvideManifestoDaoFactory.provideManifestoDao(singletonCImpl.provideDatabaseProvider.get());

          case 13: // net.wetheGoverned.repository.DistrictRepositoryImpl 
          return (T) new DistrictRepositoryImpl(singletonCImpl.provideDistrictDaoProvider.get(), singletonCImpl.provideMetricDaoProvider.get(), singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.bindDispatcherProvider.get());

          case 14: // net.wetheGoverned.local.dao.DistrictDao 
          return (T) CivicDatabaseModule_ProvideDistrictDaoFactory.provideDistrictDao(singletonCImpl.provideDatabaseProvider.get());

          case 15: // net.wetheGoverned.local.dao.MetricDao 
          return (T) CivicDatabaseModule_ProvideMetricDaoFactory.provideMetricDao(singletonCImpl.provideDatabaseProvider.get());

          case 16: // net.wetheGoverned.remote.backend.WtgBackendApi 
          return (T) CivicNetworkModule_ProvideWtgBackendApiFactory.provideWtgBackendApi();

          case 17: // net.wetheGoverned.ui.location.LocationHelper 
          return (T) new LocationHelper(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 18: // net.wetheGoverned.repository.ScorecardRepositoryImpl 
          return (T) new ScorecardRepositoryImpl(singletonCImpl.provideScorecardDaoProvider.get(), singletonCImpl.provideMetricDaoProvider.get(), singletonCImpl.provideCivicApiProvider.get(), singletonCImpl.provideWsCivicPublisherProvider.get(), singletonCImpl.bindDispatcherProvider.get());

          case 19: // net.wetheGoverned.local.dao.ScorecardDao 
          return (T) CivicDatabaseModule_ProvideScorecardDaoFactory.provideScorecardDao(singletonCImpl.provideDatabaseProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
