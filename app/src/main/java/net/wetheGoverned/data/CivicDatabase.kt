package net.wetheGoverned.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.local.entity.*

@Database(
    entities = [
        DistrictEntity::class,
        ResidentProfileEntity::class,
        DistrictPollEntity::class,
        PollPostEntity::class,
        CivicVoteEntity::class,
        RepresentativeScorecardEntity::class,
        ScorecardCategoryEntity::class,
        CandidateManifestoEntity::class,
        ManifestoQuestionEntity::class,
        DistrictMetricEntity::class,
        PendingCivicEventEntity::class,
        AccountEntity::class,
        CommunityPostEntity::class,
        VerificationRequestEntity::class,
    ],
    version = 9, // Incremented to version 9 for Verification Requests
    exportSchema = false
)
@TypeConverters(CivicConverters::class)
abstract class CivicDatabase : RoomDatabase() {

    abstract fun districtDao(): DistrictDao
    abstract fun residentProfileDao(): ResidentProfileDao
    abstract fun pollDao(): PollDao
    abstract fun pollPostDao(): PollPostDao
    abstract fun voteDao(): VoteDao
    abstract fun scorecardDao(): ScorecardDao
    abstract fun manifestoDao(): ManifestoDao
    abstract fun metricDao(): MetricDao
    abstract fun pendingEventDao(): PendingEventDao
    abstract fun accountDao(): AccountDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun verificationRequestDao(): VerificationRequestDao

    companion object {
        const val DATABASE_NAME = "wetheGoverned.db"
    }
}
