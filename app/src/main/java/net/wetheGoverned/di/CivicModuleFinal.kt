package net.wetheGoverned.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.wetheGoverned.BuildConfig
import net.wetheGoverned.core.*
import net.wetheGoverned.data.CivicDatabase
import net.wetheGoverned.data.MeshDiscoveryManager
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.data.CivicApiImpl
import net.wetheGoverned.data.repository.*
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.session.CredentialsManager
import net.wetheGoverned.session.PendingEventQueue
import net.wetheGoverned.session.AndroidPendingEventQueue
import net.wetheGoverned.zk.ZkProver
import net.wetheGoverned.zk.NativeZkProver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CivicDatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CivicDatabase =
        Room.databaseBuilder(context, CivicDatabase::class.java, CivicDatabase.DATABASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // ERR_013 FIX: Enable WAL
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton fun provideDistrictDao(db: CivicDatabase)  = db.districtDao()
    @Provides @Singleton fun provideProfileDao(db: CivicDatabase)   = db.residentProfileDao()
    @Provides @Singleton fun providePollDao(db: CivicDatabase)      = db.pollDao()
    @Provides @Singleton fun providePollPostDao(db: CivicDatabase)  = db.pollPostDao()
    @Provides @Singleton fun provideVoteDao(db: CivicDatabase)      = db.voteDao()
    @Provides @Singleton fun provideScorecardDao(db: CivicDatabase) = db.scorecardDao()
    @Provides @Singleton fun provideManifestoDao(db: CivicDatabase) = db.manifestoDao()
    @Provides @Singleton fun provideMetricDao(db: CivicDatabase)    = db.metricDao()
    @Provides @Singleton fun providePendingDao(db: CivicDatabase)   = db.pendingEventDao()
    @Provides @Singleton fun provideAccountDao(db: CivicDatabase)   = db.accountDao()
    @Provides @Singleton fun provideCommunityDao(db: CivicDatabase) = db.communityPostDao()
    @Provides @Singleton fun provideVerificationRequestDao(db: CivicDatabase) = db.verificationRequestDao()
}

@Module
@InstallIn(SingletonComponent::class)
object CivicNetworkModule {

    @Provides @Singleton
    fun provideRelayUrls(): List<String> = listOf(
        BuildConfig.RELAY_URL,
        "wss://relay.damus.io",
        "wss://nos.lol",
        "wss://relay.primal.net",
    )

    @Provides @Singleton
    fun provideCivicApi(): CivicApi = CivicApiImpl()

    @Provides @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }

    @Provides @Singleton
    fun provideWtgBackendApi(httpClient: HttpClient): WtgBackendApi = WtgBackendApi(httpClient = httpClient)

    @Provides @Singleton
    fun provideSessionManager(storage: CredentialsManager): SessionManager = SessionManager(storage)

    @Provides @Singleton
    fun provideLocationHelper(@ApplicationContext context: Context): net.wetheGoverned.ui.location.LocationHelper =
        net.wetheGoverned.ui.location.LocationHelper(context)

    @Provides @Singleton
    fun provideMeshDiscoveryManager(): MeshDiscoveryManager = net.wetheGoverned.data.AndroidMeshDiscoveryManager()

    @Provides @Singleton
    fun provideP2PSyncEngine(
        pollRepository: PollRepository,
        residentRepository: ResidentRepository,
        voteRepository: VoteRepository,
        manifestoRepository: ManifestoRepository,
        accountRepository: AccountRepository,
        sessionManager: SessionManager,
        relayManager: net.wetheGoverned.data.NostrRelayManager
    ): net.wetheGoverned.data.P2PSyncEngine = net.wetheGoverned.data.P2PSyncEngine(
        pollRepository, residentRepository, voteRepository, manifestoRepository, accountRepository, sessionManager, relayManager
    )

    @Provides @Singleton
    fun provideNostrRelayManager(relayUrls: List<String>): net.wetheGoverned.data.NostrRelayManager =
        net.wetheGoverned.data.NostrRelayManager(relayUrls)

    @Provides @Singleton
    fun provideWsCivicPublisher(
        relayManager: net.wetheGoverned.data.NostrRelayManager,
        sessionManager: SessionManager,
        pendingQueue: PendingEventQueue,
        zkProver: ZkProver,
    ): CivicPublisher = net.wetheGoverned.data.WsCivicPublisher(
        relayManager, sessionManager, pendingQueue, zkProver
    )
}

@Module
@InstallIn(SingletonComponent::class)
interface CivicRepositoryBindingModule {
    @Binds @Singleton fun bindAccountRepository(impl: AndroidAccountRepository): AccountRepository
    @Binds @Singleton fun bindPollRepository(impl: PollRepositoryImpl): PollRepository
    @Binds @Singleton fun bindScorecardRepository(impl: ScorecardRepositoryImpl): ScorecardRepository
    @Binds @Singleton fun bindManifestoRepository(impl: ManifestoRepositoryImpl): ManifestoRepository
    @Binds @Singleton fun bindResidentRepository(impl: ResidentRepositoryImpl): ResidentRepository
    @Binds @Singleton fun bindVoteRepository(impl: VoteRepositoryImpl): VoteRepository
    @Binds @Singleton fun bindDistrictRepository(impl: DistrictRepositoryImpl): DistrictRepository
    @Binds @Singleton fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository
    @Binds @Singleton fun bindVerificationRequestRepository(impl: VerificationRequestRepositoryImpl): VerificationRequestRepository
    @Binds @Singleton fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
    @Binds @Singleton fun bindZkProver(impl: NativeZkProver): ZkProver
    @Binds @Singleton fun bindPendingEventQueue(impl: AndroidPendingEventQueue): PendingEventQueue
}
