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
        RepresentativeScorecardEntity::class,
        ScorecardCategoryEntity::class,
        CandidateManifestoEntity::class,
        ManifestoQuestionEntity::class,
        DistrictMetricEntity::class,
        PendingCivicEventEntity::class,
    ],
    version = 4, // Incremented to version 4 for addressFingerprint addition
    exportSchema = false
)
@TypeConverters(CivicConverters::class)
abstract class CivicDatabase : RoomDatabase() {

    abstract fun districtDao(): DistrictDao
    abstract fun residentProfileDao(): ResidentProfileDao
    abstract fun pollDao(): PollDao
    abstract fun pollPostDao(): PollPostDao
    abstract fun scorecardDao(): ScorecardDao
    abstract fun manifestoDao(): ManifestoDao
    abstract fun metricDao(): MetricDao
    abstract fun pendingEventDao(): PendingEventDao

    companion object {
        const val DATABASE_NAME = "wetheGoverned.db"
    }
}
