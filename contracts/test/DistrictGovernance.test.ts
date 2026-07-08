import { expect } from "chai";
import { ethers } from "hardhat";
import { DistrictVoterRegistry, Polls } from "../typechain-types";
import { SignerWithAddress } from "@nomicfoundation/hardhat-ethers/signers";

describe("WeTheGoverned: Smart Contract Suite", function () {
  let registry: DistrictVoterRegistry;
  let polls: Polls;
  let owner: SignerWithAddress;
  let otherAccount: SignerWithAddress;

  const DISTRICT_ID = "GA-05";
  const DUMMY_ROOT = ethers.keccak256(ethers.toUtf8Bytes("merkle_root_1"));
  const DUMMY_NULLIFIER = ethers.keccak256(ethers.toUtf8Bytes("user_nullifier_1"));
  const DUMMY_PROOF = [0, 0, 0, 0, 0, 0, 0, 0] as any; // Simplified for placeholder verification logic

  beforeEach(async function () {
    [owner, otherAccount] = await ethers.getSigners();

    const VoterRegistryFactory = await ethers.getContractFactory("DistrictVoterRegistry");
    registry = await VoterRegistryFactory.deploy();

    const PollsFactory = await ethers.getContractFactory("Polls");
    polls = await PollsFactory.deploy(await registry.getAddress());
  });

  describe("DistrictVoterRegistry", function () {
    it("Should allow the owner to update a district root", async function () {
      await expect(registry.updateDistrictRoot(DISTRICT_ID, DUMMY_ROOT))
        .to.emit(registry, "RootUpdated")
        .withArgs(DISTRICT_ID, DUMMY_ROOT);

      expect(await registry.districtRoots(DISTRICT_ID)).to.equal(DUMMY_ROOT);
    });

    it("Should fail if a non-owner tries to update a root", async function () {
      await expect(
        registry.connect(otherAccount).updateDistrictRoot(DISTRICT_ID, DUMMY_ROOT)
      ).to.be.revertedWithCustomError(registry, "OwnableUnauthorizedAccount");
    });
  });

  describe("Polls & Anonymous Voting", function () {
    beforeEach(async function () {
      await registry.updateDistrictRoot(DISTRICT_ID, DUMMY_ROOT);
      await polls.createPoll(DISTRICT_ID, "ipfs://metadata_hash", 3600);
    });

    it("Should allow a valid voter to cast a vote", async function () {
      const pollId = 0;
      const optionId = 1;

      await expect(polls.castVote(pollId, optionId, DUMMY_NULLIFIER, DUMMY_PROOF))
        .to.emit(polls, "VoteCast")
        .withArgs(pollId, optionId);

      expect(await polls.getResults(pollId, optionId)).to.equal(1);
    });

    it("Should prevent double-voting with the same nullifier", async function () {
      const pollId = 0;
      await polls.castVote(pollId, 1, DUMMY_NULLIFIER, DUMMY_PROOF);

      await expect(
        polls.castVote(pollId, 2, DUMMY_NULLIFIER, DUMMY_PROOF)
      ).to.be.revertedWith("Already voted in this poll");
    });

    it("Should fail if the district root is not set", async function () {
      await polls.createPoll("UNKNOWN-DISTRICT", "ipfs://metadata", 3600);
      const pollId = 1;

      await expect(
        polls.castVote(pollId, 1, DUMMY_NULLIFIER, DUMMY_PROOF)
      ).to.be.revertedWith("District root not set");
    });
  });
});
