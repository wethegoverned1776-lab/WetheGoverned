// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "./DistrictVoterRegistry.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";

/**
 * @title Polls
 * @dev Handles anonymous voting using ZK-proofs verified against the DistrictVoterRegistry.
 */
contract Polls is ReentrancyGuard {
    DistrictVoterRegistry public registry;

    struct Poll {
        string districtId;
        string metadataUri; // IPFS/Arweave hash for poll questions/options
        uint256 startTime;
        uint256 endTime;
        bool isActive;
        mapping(uint256 => uint256) voteCounts; // OptionID => Count
    }

    uint256 public nextPollId;
    mapping(uint256 => Poll) public polls;

    // Mapping to track nullifiers per poll to prevent double voting
    // pollId => nullifier => hasVoted
    mapping(uint256 => mapping(bytes32 => bool)) public pollNullifiers;

    event PollCreated(uint256 indexed pollId, string districtId, string metadataUri);
    event VoteCast(uint256 indexed pollId, uint256 optionId);

    constructor(address _registry) {
        registry = DistrictVoterRegistry(_registry);
    }

    function createPoll(
        string calldata _districtId,
        string calldata _metadataUri,
        uint256 _duration
    ) external {
        uint256 pollId = nextPollId++;
        Poll storage newPoll = polls[pollId];
        newPoll.districtId = _districtId;
        newPoll.metadataUri = _metadataUri;
        newPoll.startTime = block.timestamp;
        newPoll.endTime = block.timestamp + _duration;
        newPoll.isActive = true;

        emit PollCreated(pollId, _districtId, _metadataUri);
    }

    /**
     * @dev Cast an anonymous vote using a ZK-proof.
     * @param _pollId The ID of the poll.
     * @param _optionId The ID of the chosen option.
     * @param _nullifier The unique ZK nullifier for this user + this poll.
     * @param _proof The ZK-proof data proving the user is a verified voter in the district.
     */
    function castVote(
        uint256 _pollId,
        uint256 _optionId,
        bytes32 _nullifier,
        uint256[8] calldata _proof
    ) external nonReentrant {
        Poll storage poll = polls[_pollId];
        require(poll.isActive, "Poll is not active");
        require(block.timestamp <= poll.endTime, "Poll has ended");
        require(!pollNullifiers[_pollId][_nullifier], "Already voted in this poll");

        // Verify the ZK-proof against the registry for this specific district
        bool isValidVoter = registry.verifyVoter(poll.districtId, _nullifier, _proof);
        require(isValidVoter, "Invalid voter proof");

        // Mark nullifier as used for THIS poll
        pollNullifiers[_pollId][_nullifier] = true;

        // Record vote
        poll.voteCounts[_optionId]++;

        emit VoteCast(_pollId, _optionId);
    }

    function getResults(uint256 _pollId, uint256 _optionId) external view returns (uint256) {
        return polls[_pollId].voteCounts[_optionId];
    }
}
