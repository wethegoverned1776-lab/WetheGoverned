// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "./DistrictVoterRegistry.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

/**
 * @title Instructions
 * @dev Allows verified voters to send "Direct Instructions" to their representatives.
 * Instructions are public, immutable, and proven to come from a verified constituent.
 */
contract Instructions is ReentrancyGuard {
    DistrictVoterRegistry public registry;

    struct Instruction {
        string districtId;
        string contentHash; // Arweave/IPFS hash of the instruction text
        uint256 timestamp;
        address sender; // Optional: can be 0x0 if fully anonymous via ZK
    }

    uint256 public nextInstructionId;
    mapping(uint256 => Instruction) public instructions;

    event InstructionSubmitted(
        uint256 indexed instructionId,
        string districtId,
        string contentHash
    );

    constructor(address _registry) {
        registry = DistrictVoterRegistry(_registry);
    }

    /**
     * @dev Submit an instruction with a ZK-proof of district residency.
     * @param _districtId The district the representative belongs to.
     * @param _contentHash Permanent storage hash of the instruction content.
     * @param _nullifier Anti-spam/Anti-sybil nullifier.
     * @param _proof ZK-proof data.
     */
    function submitInstruction(
        string calldata _districtId,
        string calldata _contentHash,
        bytes32 _nullifier,
        uint256[8] calldata _proof
    ) external nonReentrant {
        // Verify user is a constituent
        bool isValidVoter = registry.verifyVoter(_districtId, _nullifier, _proof);
        require(isValidVoter, "Proof of residency failed");

        uint256 id = nextInstructionId++;
        instructions[id] = Instruction({
            districtId: _districtId,
            contentHash: _contentHash,
            timestamp: block.timestamp,
            sender: msg.sender // Storing msg.sender for accountability, but ZK proof ensures residency
        });

        emit InstructionSubmitted(id, _districtId, _contentHash);
    }
}
