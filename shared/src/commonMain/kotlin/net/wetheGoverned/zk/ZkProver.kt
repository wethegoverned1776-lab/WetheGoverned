package net.wetheGoverned.zk

/**
 * Result of a ZK proof generation.
 * [proof] corresponds to the uint256[8] array used by Solidity verifiers.
 * [publicSignals] are the public inputs used during proof generation.
 */
data class ZkProofResult(
    val proof: List<String>,
    val publicSignals: List<String>
)

interface ZkProver {
    /**
     * Generates a ZK proof for district residency.
     * @param circuitName The name of the circuit (e.g., "voter_nostr")
     * @param inputs Map of private and public inputs for the circuit.
     */
    suspend fun generateProof(
        circuitName: String,
        inputs: Map<String, Any>
    ): ZkProofResult
}
