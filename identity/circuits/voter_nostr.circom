pragma circom 2.1.0;

include "node_modules/circomlib/circuits/poseidon.circom";
include "node_modules/circomlib/circuits/merkleTree.circom";

/**
 * @title VoterNostrBridge
 * @dev Proves that a specific Nostr Public Key belongs to a verified voter.
 */
template VoterNostrBridge(levels) {
    // Public Inputs
    signal input merkleRoot;
    signal input nullifierHash;
    signal input nostrPubKey; // Binding the proof to a Nostr Identity

    // Private Inputs
    signal input secret;
    signal input pathElements[levels];
    signal input pathIndices[levels];

    // 1. Verify that H(secret) is the identity commitment in the Merkle Tree
    component hasher = Poseidon(1);
    hasher.inputs[0] <== secret;
    signal identityCommitment <== hasher.out;

    component tree = MerkleTreeInclusionProof(levels);
    tree.leaf <== identityCommitment;
    tree.root <== merkleRoot;
    for (var i = 0; i < levels; i++) {
        tree.pathElements[i] <== pathElements[i];
        tree.pathIndices[i] <== pathIndices[i];
    }

    // 2. Bind the proof to the Nostr Public Key
    // This ensures the proof cannot be "intercepted" and used by another Nostr account.
    component binder = Poseidon(2);
    binder.inputs[0] <== secret;
    binder.inputs[1] <== nostrPubKey;

    // We don't reveal the secret, but the nullifierHash must be derived
    // from the secret to prevent reuse.
    nullifierHash === binder.out;
}

component main {public [merkleRoot, nullifierHash, nostrPubKey]} = VoterNostrBridge(20);
