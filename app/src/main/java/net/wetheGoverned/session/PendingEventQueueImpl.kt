package net.wetheGoverned.session

import net.wetheGoverned.local.dao.PendingEventDao
import net.wetheGoverned.local.entity.PendingCivicEventEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPendingEventQueue @Inject constructor(
    private val pendingEventDao: PendingEventDao
) : PendingEventQueue {
    override suspend fun enqueue(kind: Int, contentJson: String, sig: String) {
        val entity = PendingCivicEventEntity(
            eventId = "pend_${System.currentTimeMillis()}",
            kind = kind,
            contentJson = contentJson,
            sig = sig,
            createdAt = System.currentTimeMillis()
        )
        pendingEventDao.enqueue(entity)
    }

    override suspend fun getAllPending(): List<PendingEvent> {
        return pendingEventDao.getAllPending().map {
            PendingEvent(
                eventId = it.eventId,
                kind = it.kind,
                contentJson = it.contentJson,
                sig = it.sig,
                createdAt = it.createdAt
            )
        }
    }

    override suspend fun dequeue(eventId: String) {
        pendingEventDao.dequeue(eventId)
    }
}
