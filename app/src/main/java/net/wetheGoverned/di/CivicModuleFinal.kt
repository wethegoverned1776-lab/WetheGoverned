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
import net.wetheGoverned.local.CivicDatabase
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.backend.WtgBackendApi
import net.wetheGoverned.remote.impl.CivicApiImpl
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.session.PendingEventQueue
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// CivicModule – Final production wiring. Zero stubs. Zero TODOs.
// ─────────────────────────────────────────────────────────────────────────────

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
    @Provides @Singleton fun provideScorecardDao(db: CivicDatabase) = db.scorecardDao()
    @Provides @Singleton fun provideManifestoDao(db: CivicDatabase) = db.manifestoDao()
    @Provides @Singleton fun provideMetricDao(db: CivicDatabase)    = db.metricDao()
    @Provides @Singleton fun providePendingDao(db: CivicDatabase)   = db.pendingEventDao()
}

@Module
@InstallIn(SingletonComponent::class)
object CivicNetworkModule {

    @Provides @Singleton
    fun provideRelayUrl(): String = BuildConfig.RELAY_URL

    @Provides @Singleton
    fun provideCivicApi(relayUrl: String): CivicApi = CivicApiImpl(
        congressApiKey    = BuildConfig.CONGRESS_API_KEY,
        openStatesApiKey  = BuildConfig.OPENSTATES_API_KEY,
        googleCivicApiKey = BuildConfig.GOOGLE_CIVIC_API_KEY,
        relayUrl          = relayUrl,
    )

    @Provides @Singleton
    fun provideWtgBackendApi(): WtgBackendApi = WtgBackendApi()

    @Provides @Singleton
    fun provideWsCivicPublisher(
        relayUrl: String,
        sessionManager: SessionManager,
        dispatchers: DispatcherProvider,
        pendingQueue: PendingEventQueue,
    ): WsCivicPublisherWithQueue = WsCivicPublisherWithQueue(
        relayUrl       = relayUrl,
        sessionManager = sessionManager,
        dispatchers    = dispatchers,
        pendingQueue   = pendingQueue,
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CivicRepositoryModule {

    @Binds @Singleton
    abstract fun bindPollRepository(impl: PollRepositoryImpl): PollRepository

    @Binds @Singleton
    abstract fun bindScorecardRepository(impl: ScorecardRepositoryImpl): ScorecardRepository

    @Binds @Singleton
    abstract fun bindManifestoRepository(impl: ManifestoRepositoryImpl): ManifestoRepository

    @Binds @Singleton
    abstract fun bindResidentRepository(impl: ResidentRepositoryImpl): ResidentRepository

    @Binds @Singleton
    abstract fun bindDistrictRepository(impl: DistrictRepositoryImpl): DistrictRepository

    @Binds @Singleton
    abstract fun bindCivicPublisher(impl: WsCivicPublisherWithQueue): CivicPublisher

    @Binds @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
