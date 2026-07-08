package net.wetheGoverned.session

import net.wetheGoverned.model.CivicEvent

interface PendingEventQueue {
    suspend fun enqueue(kind: Int, contentJson: String, sig: String)
    suspend fun getAllPending(): List<PendingEvent>
    suspend fun dequeue(eventId: String)
}

data class PendingEvent(
    val eventId: String,
    val kind: Int,
    val contentJson: String,
    val sig: String,
    val createdAt: Long
)
