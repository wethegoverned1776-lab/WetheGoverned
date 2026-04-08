package net.wetheGoverned.remote.api

import net.wetheGoverned.model.*

// ─────────────────────────────────────────────────────────────────────────────
// CivicApi  (replaces Primal's FeedApi + UsersApi)
//
// Sources:
//   - WeTheGoverned relay  (wss://relay.wetheGoverned.net)  – Nostr events
//   - Congress.gov API     (https://api.congress.gov/v3)    – official votes / attendance
//   - OpenStates API       (https://v3.openstates.org)      – state-level data
//   - Google Civic Info    (https://civicinfo.googleapis.com) – district boundaries
//
// The implementation uses PrimalApiClient (Ktor WebSocket) for relay calls and
// standard Ktor HTTP for REST endpoints, matching Primal's remote module pattern.
// ─────────────────────────────────────────────────────────────────────────────

interface CivicApi {

    // ── Polls (Nostr relay) ───────────────────────────────────────────────────

    suspend fun fetchPolls(
        districtId: String,
        limit: Int = 20,
        before: Long? = null,
    ): List<DistrictPoll>

    suspend fun fetchPoll(pollId: String): DistrictPoll

    suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
    ): DistrictPoll

    // ── Representative scorecard (Congress.gov + relay) ───────────────────────

    /**
     * Fetches:
     *   - Official attendance/voting record from Congress.gov
     *   - Resident-submitted metric reports from the Nostr relay
     * Merges into a RepresentativeScorecard.
     */
    suspend fun fetchScorecard(districtId: String): RepresentativeScorecard

    // ── District metrics ──────────────────────────────────────────────────────

    suspend fun fetchMetrics(districtId: String): List<DistrictMetric>

    // ── Manifestos (Nostr relay, kind = 30_300) ───────────────────────────────

    suspend fun fetchManifestos(districtId: String): List<CandidateManifesto>

    suspend fun fetchManifesto(manifestoId: String): CandidateManifesto

    // ── Resident profiles ─────────────────────────────────────────────────────

    suspend fun fetchProfile(pubKey: String): ResidentProfile

    suspend fun upgradeTier(
        pubKey: String,
        proofToken: String,
        targetTier: VerificationTier,
    ): ResidentProfile

    // ── District geometry (Google Civic Info) ─────────────────────────────────

    suspend fun fetchDistrict(districtId: String): District

    /**
     * Detect the congressional district for a lat/lng coordinate.
     * Used during onboarding to auto-assign the resident to their district.
     */
    suspend fun detectDistrict(latitude: Double, longitude: Double): District
}
