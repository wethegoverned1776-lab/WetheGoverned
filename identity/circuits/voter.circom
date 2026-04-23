pragma circom 2.1.0;

include "node_modules/circomlib/circuits/poseidon.circom";
include "node_modules/circomlib/circuits/merkleTree.circom";

/**
 * @title VoterProof
 * @dev Proves membership in a district's verified voter pool without revealing identity.
 */
template VoterProof(levels) {
    // Public Inputs
    signal input merkleRoot;
    signal input nullifierHash; // Hash(Secret + ActionID) to prevent double-voting

    // Private Inputs
    signal input secret;
    signal input pathElements[levels];
    signal input pathIndices[levels];

    // 1. Derive Identity Commitment from Secret
    component hasher = Poseidon(1);
    hasher.inputs[0] <== secret;
    signal identityCommitment <== hasher.out;

    // 2. Verify Merkle Membership
    component tree = MerkleTreeInclusionProof(levels);
    tree.leaf <== identityCommitment;
    tree.root <== merkleRoot;
    for (var i = 0; i < levels; i++) {
        tree.pathElements[i] <== pathElements[i];
        tree.pathIndices[i] <== pathIndices[i];
    }

    // 3. Constrain Nullifier to prevent double-voting
    // nullifierHash = Poseidon(secret + actionId) - actionId passed from outside logic
    component nullifierDeriver = Poseidon(2);
    nullifierDeriver.inputs[0] <== secret;
    nullifierDeriver.inputs[1] <== 12345; // Placeholder for ActionID (e.g., PollID)
    nullifierHash === nullifierDeriver.out;
}

component main {public [merkleRoot, nullifierHash]} = VoterProof(20);
