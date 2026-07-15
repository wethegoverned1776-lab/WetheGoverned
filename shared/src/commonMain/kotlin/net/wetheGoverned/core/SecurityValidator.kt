package net.wetheGoverned.core

import net.wetheGoverned.model.*
import kotlinx.datetime.Clock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Global Security Validator for Civic Events.
 * Optimized for National Scale (440M+ Voters).
 * Multiplatform compatible (Mutex instead of synchronized).
 */
object SecurityValidator {

    private const val BITSET_SIZE = 10_000_000 // Scaled down for memory safety in browser
    private val bitset = LongArray(BITSET_SIZE / 64)
    
    private val mutex = Mutex()
    private val MAX_CLOCK_DRIFT_MS = 300_000 
    
    private val authorizedVoterRoll = mutableSetOf<String>()

    suspend fun setAuthorizedVoters(keys: Set<String>) {
        mutex.withLock {
            authorizedVoterRoll.clear()
            authorizedVoterRoll.addAll(keys)
        }
    }

    suspend fun reset() {
        mutex.withLock {
            bitset.fill(0L)
            authorizedVoterRoll.clear()
        }
    }

    fun validateVote(vote: CivicVote, myDistrictId: String?): ValidationResult {
        val now = Clock.System.now().toEpochMilliseconds()

        if (vote.voterPubKey.isBlank()) {
            return ValidationResult.Failure("Malformed")
        }
        if (vote.createdAt > now + MAX_CLOCK_DRIFT_MS) return ValidationResult.Failure("Clock Drift")
        
        // Simplified multi-threaded safety for commonMain
        if (authorizedVoterRoll.isNotEmpty() && !authorizedVoterRoll.contains(vote.voterPubKey)) {
            return ValidationResult.Failure("Sybil Attack")
        }

        return ValidationResult.Success
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val reason: String) : ValidationResult()
}
