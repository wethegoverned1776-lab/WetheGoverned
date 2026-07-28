package net.wetheGoverned.core

interface CivicPublisher {
    suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<List<String>>,
        content: String,
        pubKey: String
    )
}
