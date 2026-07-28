package net.wetheGoverned.model

import kotlinx.serialization.Serializable

/**
 * NIP-11: Relay Information Document
 */
@Serializable
data class RelayInfo(
    val name: String? = null,
    val description: String? = null,
    val pubkey: String? = null,
    val contact: String? = null,
    val supported_nips: List<Int>? = null,
    val software: String? = null,
    val version: String? = null,
    val limitation: RelayLimitation? = null,
    val payments_url: String? = null,
    val fees: RelayFees? = null
)

@Serializable
data class RelayLimitation(
    val max_message_length: Int? = null,
    val max_subscriptions: Int? = null,
    val max_filters: Int? = null,
    val max_limit: Int? = null,
    val max_subid_length: Int? = null,
    val min_prefix: Int? = null,
    val max_event_tags: Int? = null,
    val max_content_length: Int? = null,
    val auth_required: Boolean? = false,
    val payment_required: Boolean? = false
)

@Serializable
data class RelayFees(
    val admission: List<RelayFee>? = null,
    val subscription: List<RelayFee>? = null,
    val publication: List<RelayFee>? = null
)

@Serializable
data class RelayFee(
    val amount: Long,
    val unit: String
)

/**
 * NIP-65: Relay List Metadata
 */
data class UserRelayList(
    val pubkey: String,
    val relays: List<RelayUsage>
)

data class RelayMetadata(
    val url: String,
    val read: Boolean = true,
    val write: Boolean = true
)

/**
 * Relay Quality and Scoring
 */
@Serializable
data class RelayMetric(
    val url: String,
    val rtt: Long = -1,
    val lastSeen: Long = 0,
    val isOnline: Boolean = false,
    val isPaid: Boolean = false,
    val info: RelayInfo? = null,
    val score: Int = 0
)

typealias RelayUsage = RelayMetadata
