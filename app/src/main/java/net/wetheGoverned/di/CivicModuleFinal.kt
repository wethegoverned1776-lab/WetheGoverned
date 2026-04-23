package net.wetheGoverned.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wetheGoverned.BuildConfig
import net.wetheGoverned.core.*
import net.wetheGoverned.data.CivicDatabase
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.backend.WtgBackendApi
import net.wetheGoverned.remote.impl.CivicApiImpl
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.session.PendingEventQueue
import net.wetheGoverned.zk.ZkProver
import net.wetheGoverned.zk.NativeZkProver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CivicDatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CivicDatabase =
        Room.databaseBuilder(context, CivicDatabase::class.java, CivicDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton fun provideDistrictDao(db: CivicDatabase)  = db.districtDao()
    @Provides @Singleton fun provideProfileDao(db: CivicDatabase)   = db.residentProfileDao()
    @Provides @Singleton fun providePollDao(db: CivicDatabase)      = db.pollDao()
    @Provides @Singleton fun providePollPostDao(db: CivicDatabase)  = db.pollPostDao()
    @Provides @Singleton fun provideScorecardDao(db: CivicDatabase) = db.scorecardDao()
    @Provides @Singleton fun provideManifestoDao(db: CivicDatabase) = db.manifestoDao()
    @Provides @Singleton fun provideMetricDao(db: CivicDatabase)    = db.metricDao()
    @Provides @Singleton fun providePendingDao(db: CivicDatabase)   = db.pendingEventDao()
}

@Module
@InstallIn(SingletonComponent::class)
object CivicNetworkModule {

    @Provides @Singleton
    fun provideRelayUrls(): List<String> = listOf(
        BuildConfig.RELAY_URL,
        "wss://relay.damus.io",
        "wss://nos.lol",
        "wss://relay.primal.net"
    )

    @Provides @Singleton
    fun provideCivicApi(relayUrls: List<String>): CivicApi = CivicApiImpl(
        congressApiKey    = BuildConfig.CONGRESS_API_KEY,
        openStatesApiKey  = BuildConfig.OPENSTATES_API_KEY,
        googleCivicApiKey = BuildConfig.GOOGLE_CIVIC_API_KEY,
        relayUrls         = relayUrls,
    )

    @Provides @Singleton
    fun provideWtgBackendApi(): WtgBackendApi = WtgBackendApi()

    @Provides @Singleton
    fun provideWsCivicPublisher(
        relayUrls: List<String>,
        sessionManager: SessionManager,
        dispatchers: DispatcherProvider,
        pendingQueue: PendingEventQueue,
        zkProver: ZkProver,
    ): WsCivicPublisherWithQueue = WsCivicPublisherWithQueue(
        relayUrls      = relayUrls,
        sessionManager = sessionManager,
        dispatchers    = dispatchers,
        pendingQueue   = pendingQueue,
        zkProver       = zkProver,
    )
}

@Module
@InstallIn(SingletonComponent::class)
interface CivicRepositoryBindingModule {
    @Binds @Singleton fun bindPollRepository(impl: PollRepositoryImpl): PollRepository
    @Binds @Singleton fun bindScorecardRepository(impl: ScorecardRepositoryImpl): ScorecardRepository
    @Binds @Singleton fun bindManifestoRepository(impl: ManifestoRepositoryImpl): ManifestoRepository
    @Binds @Singleton fun bindResidentRepository(impl: ResidentRepositoryImpl): ResidentRepository
    @Binds @Singleton fun bindDistrictRepository(impl: DistrictRepositoryImpl): DistrictRepository
    @Binds @Singleton fun bindCivicPublisher(impl: WsCivicPublisherWithQueue): CivicPublisher
    @Binds @Singleton fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
    @Binds @Singleton fun bindZkProver(impl: NativeZkProver): ZkProver
}
