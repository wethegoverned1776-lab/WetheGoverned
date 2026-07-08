package net.wetheGoverned.model

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CivicVoteTest {

    @Test
    fun createUniqueVotes_shouldHaveDifferentNonces() {
        val vote1 = CivicVote(
            id = "v1", pollId = "p1", voterPubKey = "pub1", voterName = "Alice",
            optionId = "opt1", timestamp = 1715000000L, nonce = 12345L
        )
        
        val vote2 = vote1.copy(id = "v2", nonce = 12346L)
        
        assertNotEquals(vote1.nonce, vote2.nonce, "Consecutive votes must have unique nonces to prevent replay")
    }

    @Test
    fun voteTimestamp_shouldBeValid() {
        val now = System.currentTimeMillis()
        val vote = CivicVote(
            id = "v1", pollId = "p1", voterPubKey = "pub1", voterName = "Alice",
            optionId = "opt1", timestamp = now, nonce = 999L
        )
        
        assertTrue(vote.timestamp > 0, "Vote timestamp must be a positive long")
    }
}
