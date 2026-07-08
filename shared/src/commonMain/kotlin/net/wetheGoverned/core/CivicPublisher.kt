package net.wetheGoverned.core

interface CivicPublisher {
    suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<String>,
        content: String,
        pubKey: String
    )
}
