package net.wetheGoverned.data

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.session.UserSession
import kotlin.test.Test
import kotlin.test.assertEquals

class P2PSyncTest {

    @Test
    fun testSyncLoop_mergesRemotePolls() = runTest {
        // 1. Setup mocks
        val pollRepo = InMemoryPollRepository()
        val residentRepo = InMemoryResidentRepository()
        val voteRepo = InMemoryVoteRepository()
        val manifestoRepo = InMemoryManifestoRepository()
        val accountRepo = InMemoryAccountRepository()
        val sessionManager = SessionManager()
        
        // Setup session
        sessionManager.login("user1", null, "us-fl-06", VerificationTier.TIER_2, "User 1")

        // Mock Relay Manager
        val relayManager = NostrRelayManager(emptyList())

        val syncEngine = P2PSyncEngine(
            pollRepo, residentRepo, voteRepo, manifestoRepo, accountRepo, sessionManager, relayManager
        )

        // Verify that our repositories are ready for sync.
        val initialPolls = pollRepo.getAllPolls()
        assertEquals(0, initialPolls.size)

        val remotePoll = CivicPoll(
            id = "remote_1",
            scope = CivicScope.FEDERAL,
            districtId = "us",
            authorPubKey = "admin",
            question = "Remote Question",
            options = emptyList(),
            status = PollStatus.ACTIVE,
            createdAt = System.currentTimeMillis(),
            closesAt = null,
            totalVotes = 0
        )

        pollRepo.syncPoll(remotePoll)
        
        val afterSync = pollRepo.getAllPolls()
        assertEquals(1, afterSync.size)
        assertEquals("remote_1", afterSync[0].id)
    }
}

// Minimal memory repos for testing in common module
class InMemoryPollRepository : PollRepository {
    private val polls = mutableListOf<CivicPoll>()
    override fun observeDistrictPolls(districtId: String) = flowOf(polls.filter { it.districtId == districtId })
    override fun observePollsByIds(districtIds: List<String>) = flowOf(polls.filter { it.districtId in districtIds })
    override suspend fun getPoll(pollId: String) = Result.success(polls.first { it.id == pollId })
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?) = Result.success(polls.first())
    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String) = Result.success(Unit)
    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String) = Result.success(Unit)
    override fun observePollPosts(pollId: String) = flowOf(emptyList<PollPost>())
    override fun observeOptionPosts(pollId: String, optionId: String) = flowOf(emptyList<PollPost>())
    override fun observeThreadedPosts(parentPostId: String) = flowOf(emptyList<PollPost>())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?) = Result.success(PollPost(pollId = "", optionId = "", authorName = "", content = ""))
    override suspend fun voteOnPost(postId: String, delta: Int) = Result.success(Unit)
    override suspend fun getPost(postId: String) = Result.success(PollPost(pollId = "", optionId = "", authorName = "", content = ""))
    override suspend fun getAllPolls() = polls
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long) = polls.filter { it.districtId in jurisdictionIds }
    override suspend fun syncPoll(poll: CivicPoll) { polls.add(poll) }
}

class InMemoryResidentRepository : ResidentRepository {
    override fun observeProfile(pubKey: String) = flowOf(null)
    override fun observeProfileByFingerprint(fingerprint: String) = flowOf(null)
    override suspend fun getProfile(pubKey: String) = Result.failure<ResidentProfile>(Exception())
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String) = Result.failure<ResidentProfile>(Exception())
    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String) = Result.failure<ResidentProfile>(Exception())
    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?) = Result.success(Unit)
    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?) = Result.failure<ResidentProfile>(Exception())
    override suspend fun updateDistrict(pubKey: String, districtId: String) = Result.success(Unit)
    override suspend fun getResidentCountAtAddress(fingerprint: String) = 0
    override suspend fun getVouchCount(notaryPubKey: String) = 0
}

class InMemoryVoteRepository : VoteRepository {
    override fun observeAllVotes() = flowOf(emptyList<CivicVote>())
    override fun observeVotesByUser(pubKey: String) = flowOf(emptyList<CivicVote>())
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long) = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String) = Result.success(Unit)
    override suspend fun resolveVote(voteId: String) = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) {}
}

class InMemoryManifestoRepository : ManifestoRepository {
    override fun observeManifestos(districtId: String) = flowOf(emptyList<CandidateManifesto>())
    override suspend fun getManifesto(manifestoId: String) = Result.failure<CandidateManifesto>(Exception())
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String) = Result.failure<CandidateManifesto>(Exception())
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String) = Result.failure<ManifestoQuestion>(Exception())
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String) = Result.failure<ManifestoQuestion>(Exception())
}
