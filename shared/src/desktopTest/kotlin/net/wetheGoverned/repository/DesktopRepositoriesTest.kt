package net.wetheGoverned.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.wetheGoverned.model.*
import net.wetheGoverned.session.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DesktopRepositoriesTest {

    @Test
    fun testDesktopPollRepository_observeDistrictPolls() = runTest {
        val repository = DesktopPollRepository()
        val polls = repository.observeDistrictPolls("us-fl-06").first()
        
        // Should include federal, and district (us-fl-06)
        assertTrue(polls.any { it.scope == CivicScope.FEDERAL }, "Federal poll not found in $polls")
        assertTrue(polls.any { it.id == "poll_1" }, "Poll 1 not found in $polls")
        assertTrue(polls.any { it.id == "poll_2" }, "Poll 2 not found in $polls")
    }

    @Test
    fun testDesktopPollRepository_observePollsByIds() = runTest {
        val repository = DesktopPollRepository()
        val hierarchy = listOf("us", "us-fl", "us-fl-06")
        val polls = repository.observePollsByIds(hierarchy).first()
        
        assertTrue(polls.any { it.districtId == "us" }, "Federal poll not found by ID")
        assertTrue(polls.any { it.districtId == "us-fl-06" }, "District poll not found by ID")
        assertEquals(3, polls.size, "Expected 3 polls (1 federal, 2 district)")
    }

    @Test
    fun testDesktopPollRepository_vote() = runTest {
        val repository = DesktopPollRepository()
        val pollId = "poll_1"
        val optionId = "opt_1"
        
        val initialPoll = repository.getPoll(pollId).getOrThrow()
        val initialVoteCount = initialPoll.options.find { it.id == optionId }?.voteCount ?: 0
        
        repository.vote(pollId, optionId, "user1")
        
        val updatedPoll = repository.getPoll(pollId).getOrThrow()
        val updatedVoteCount = updatedPoll.options.find { it.id == optionId }?.voteCount ?: 0
        
        assertEquals(initialVoteCount + 1, updatedVoteCount)
        assertEquals(optionId, updatedPoll.residentVoteOption)
    }

    @Test
    fun testDesktopResidentRepository_getProfile() = runTest {
        val repository = DesktopResidentRepository()
        val profile = repository.getProfile("admin").getOrThrow()
        
        assertEquals("admin", profile.pubKey)
        assertEquals("Administrator", profile.displayName)
        assertEquals(VerificationTier.TIER_3, profile.tier)
    }

    @Test
    fun testDesktopPollRepository_createPostAndObserve() = runTest {
        val repository = DesktopPollRepository()
        val pollId = "poll_1"
        val optionId = "opt_1"
        
        repository.createPost(pollId, optionId, "Author", "Content").getOrThrow()
        
        val posts = repository.observePollPosts(pollId).first()
        assertEquals(1, posts.size)
        assertEquals("Author", posts[0].authorName)
        assertEquals("Content", posts[0].content)
    }

    @Test
    fun testDesktopScorecardRepository_submitMetricReport() = runTest {
        val repository = DesktopScorecardRepository()
        val result = repository.submitMetricReport("us-fl-06", "Education", "Funding", "10M", "$", "user1")
        
        val metric = result.getOrThrow()
        assertEquals("Education", metric.category)
        assertEquals("Funding", metric.name)
        assertEquals("10M", metric.residentValue)
        assertEquals(MetricSource.RESIDENT_REPORTED, metric.source)
    }

    @Test
    fun testDesktopAccountRepository_persistence() = runTest {
        val repository = DesktopAccountRepository()
        val username = "testuser_${System.currentTimeMillis()}"
        val account = UserAccount(username, "password123", "pub1", "priv1", "us-wa-07")
        
        repository.register(account).getOrThrow()
        
        val loggedIn = repository.login(username, "password123").getOrThrow()
        assertEquals("pub1", loggedIn.pubKey)
        assertEquals("us-wa-07", loggedIn.districtId)
    }

    @Test
    fun testDesktopSessionStorage() {
        val storage = DesktopSessionStorage()
        val session = UserSession(
            pubKey = "key1",
            displayName = "User 1",
            districtId = "us-fl-06",
            tier = VerificationTier.TIER_2,
            privateKey = "priv1"
        )
        
        storage.saveSession(session)
        val loaded = storage.getSession()
        
        assertNotNull(loaded)
        assertEquals("key1", loaded.pubKey)
        assertEquals("priv1", loaded.privateKey)
        assertEquals(VerificationTier.TIER_2, loaded.tier)
        
        storage.clearSession()
        kotlin.test.assertNull(storage.getSession())
    }
}
