package net.wetheGoverned.local

import androidx.room.Database
import androidx.room.RoomDatabase
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.local.entity.*

// ─────────────────────────────────────────────────────────────────────────────
// CivicDatabase  (replaces Primal's AppDatabase)
//
// Version starts at 1 (fresh schema – not migrating from Primal's tables).
// Schema export path is set in build.gradle.kts, same as Primal.
// ─────────────────────────────────────────────────────────────────────────────

@Database(
    entities = [
        DistrictEntity::class,
        ResidentProfileEntity::class,
        DistrictPollEntity::class,
        RepresentativeScorecardEntity::class,
        ScorecardCategoryEntity::class,
        CandidateManifestoEntity::class,
        ManifestoQuestionEntity::class,
        DistrictMetricEntity::class,
        PendingCivicEventEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CivicDatabase : RoomDatabase() {

    abstract fun districtDao(): DistrictDao
    abstract fun residentProfileDao(): ResidentProfileDao
    abstract fun pollDao(): PollDao
    abstract fun scorecardDao(): ScorecardDao
    abstract fun manifestoDao(): ManifestoDao
    abstract fun metricDao(): MetricDao
    abstract fun pendingEventDao(): PendingEventDao

    companion object {
        const val DATABASE_NAME = "wetheGoverned.db"
    }
}
