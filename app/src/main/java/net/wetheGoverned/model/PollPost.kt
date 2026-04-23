package net.wetheGoverned.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PollPost(
    val id: String = UUID.randomUUID().toString(),
    val pollId: String,
    val optionId: String,
    val parentPostId: String? = null,
    val headline: String? = null,
    val authorName: String,
    val content: String,
    val score: Int = 0,
    val userVote: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
