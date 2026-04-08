package net.wetheGoverned.core

import net.wetheGoverned.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WsCivicPublisherWithQueue @Inject constructor(
    private val relayUrl: String,
    private val sessionManager: SessionManager,
    private val dispatchers: DispatcherProvider,
    private val pendingQueue: net.wetheGoverned.session.PendingEventQueue,
) : CivicPublisher {
    override suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<String>,
        content: String,
        pubKey: String
    ) {
        // Implementation for production publisher with queueing
        TODO("Not yet implemented")
    }
}
