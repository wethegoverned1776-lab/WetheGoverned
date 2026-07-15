package net.wetheGoverned.model

import kotlinx.serialization.Serializable
import net.wetheGoverned.core.randomUUID
import kotlinx.datetime.Clock

@Serializable
data class PollPost(
    val id: String = randomUUID(),
    val pollId: String,
    val optionId: String,
    val parentPostId: String? = null,
    val headline: String? = null,
    val authorName: String,
    val content: String,
    val score: Int = 0,
    val userVote: Int = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
