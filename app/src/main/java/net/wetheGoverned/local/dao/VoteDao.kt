package net.wetheGoverned.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.local.entity.CivicVoteEntity

@Dao
interface VoteDao {
    @Query("SELECT * FROM poll_votes ORDER BY timestamp DESC")
    fun observeAllVotes(): Flow<List<CivicVoteEntity>>

    @Query("SELECT * FROM poll_votes WHERE voterPubKey = :pubKey ORDER BY timestamp DESC")
    fun observeVotesByUser(pubKey: String): Flow<List<CivicVoteEntity>>

    @Query("SELECT * FROM poll_votes WHERE id = :voteId")
    suspend fun getVoteById(voteId: String): CivicVoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVote(vote: CivicVoteEntity)

    @Query("UPDATE poll_votes SET isFlagged = 1, status = 'FLAGGED', disputeExpiresAt = :expiresAt WHERE id = :voteId")
    suspend fun flagVote(voteId: String, expiresAt: Long)

    @Query("UPDATE poll_votes SET disputeComment = :comment, status = 'DISPUTED' WHERE id = :voteId")
    suspend fun disputeVote(voteId: String, comment: String)

    @Query("UPDATE poll_votes SET status = 'RESOLVED', isFlagged = 0 WHERE id = :voteId")
    suspend fun resolveVote(voteId: String)
}
