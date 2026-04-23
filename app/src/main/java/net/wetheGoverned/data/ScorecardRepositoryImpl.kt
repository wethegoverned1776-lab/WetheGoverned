package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import net.wetheGoverned.local.dao.MetricDao
import net.wetheGoverned.local.dao.ScorecardDao
import net.wetheGoverned.local.entity.DistrictMetricEntity
import net.wetheGoverned.local.entity.RepresentativeScorecardEntity
import net.wetheGoverned.local.entity.ScorecardCategoryEntity
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.core.CivicPublisher
import net.wetheGoverned.core.DispatcherProvider
import java.util.UUID
import javax.inject.Inject

class ScorecardRepositoryImpl @Inject constructor(
    private val scorecardDao: ScorecardDao,
    private val metricDao: MetricDao,
    private val civicApi: CivicApi,
    private val civicPublisher: CivicPublisher,
    private val dispatchers: DispatcherProvider,
) : ScorecardRepository {

    override fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?> =
        combine(
            scorecardDao.observeScorecard(districtId),
            metricDao.observeMetrics(districtId),
        ) { scorecardEntity, _ ->
            scorecardEntity?.let { buildDomain(it, districtId) }
        }

    override suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard> =
        withContext(dispatchers.io()) {
            runCatching {
                val remote = civicApi.fetchScorecard(districtId)
                scorecardDao.upsertScorecard(remote.toHeaderEntity())
                scorecardDao.deleteCategoriesForDistrict(districtId)
                scorecardDao.upsertCategories(remote.categories.map { it.toEntity(districtId) })
                remote
            }.recoverCatching {
                val cached = scorecardDao.getScorecard(districtId)
                    ?: throw IllegalStateException("No cached scorecard for $districtId")
                buildDomain(cached, districtId)
            }
        }

    override suspend fun submitMetricReport(
        districtId: String,
        category: String,
        name: String,
        value: String,
        unit: String,
        reporterPubKey: String,
    ): Result<DistrictMetric> = withContext(dispatchers.io()) {
        val metricId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val optimisticEntity = DistrictMetricEntity(
            id = metricId,
            districtId = districtId,
            category = category,
            name = name,
            officialValue = "",
            residentValue = value,
            unit = unit,
            source = MetricSource.RESIDENT_REPORTED.name,
            reportedAt = now,
            reporterPubKey = reporterPubKey,
            cachedAt = now,
        )

        metricDao.upsertMetric(optimisticEntity)

        val result = runCatching {
            civicPublisher.signPublishImportCivicEvent(
                kind = CivicEventKind.METRIC_REPORT,
                tags = listOf(
                    "district:$districtId",
                    "category:$category",
                    "metric:$name",
                    "unit:$unit",
                ),
                content = value,
                pubKey = reporterPubKey,
            )
            optimisticEntity.toDomain()
        }
        
        if (result.isFailure) {
            metricDao.evictStaleMetrics(districtId, before = now + 1)
        }
        
        result
    }

    private suspend fun buildDomain(
        entity: RepresentativeScorecardEntity,
        districtId: String,
    ): RepresentativeScorecard {
        val categories = scorecardDao.getCategoriesForDistrict(districtId)
        return RepresentativeScorecard(
            representativePubKey = entity.representativePubKey,
            districtId = entity.districtId,
            name = entity.name,
            party = entity.party,
            overallScore = entity.overallScore,
            categories = categories.map { it.toDomain() },
            lastUpdated = entity.lastUpdated,
        )
    }

    private fun RepresentativeScorecard.toHeaderEntity() = RepresentativeScorecardEntity(
        districtId = districtId,
        representativePubKey = representativePubKey,
        name = name,
        party = party,
        overallScore = overallScore,
        lastUpdated = lastUpdated,
        cachedAt = System.currentTimeMillis(),
    )

    private fun ScorecardCategory.toEntity(districtId: String) = ScorecardCategoryEntity(
        districtId = districtId,
        categoryName = name,
        officialValue = officialValue,
        residentReportedValue = residentReportedValue,
        score = score,
    )

    private fun ScorecardCategoryEntity.toDomain() = ScorecardCategory(
        name = categoryName,
        officialValue = officialValue,
        residentReportedValue = residentReportedValue,
        score = score,
    )

    private fun DistrictMetricEntity.toDomain() = DistrictMetric(
        id = id,
        districtId = districtId,
        category = category,
        name = name,
        officialValue = officialValue,
        residentValue = residentValue,
        unit = unit,
        source = MetricSource.valueOf(source),
        reportedAt = reportedAt,
        reporterPubKey = reporterPubKey,
    )
}
