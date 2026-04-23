# User Sovereignty & Security Model

## 1. Decentralized Identity (DID)
Users are identified via `did:pkh` (EIP-155) or `did:polygonid`. The platform never stores a database of usernames or profiles. All identity attributes (district, citizenship) are held by the user as **Verifiable Credentials (VCs)**.

## 2. Zero-Knowledge Voter Verification
To prevent the creation of a "government honeypot," we use ZK-proofs:
- **Proof of District:** User proves `H(Address + Secret) == Commitment` where Commitment is part of a verified district set.
- **Privacy:** The smart contract verifies the proof without ever seeing the user's physical address or legal name.

## 3. Censorship Resistance (CR)
- **Frontend:** Distributed via IPFS/Content-Addressing. No DNS-dependency for access.
- **RPC:** Defaulting to decentralized providers (e.g., Pocket Network) with fallback to user-run light clients.
- **Storage:** Poll metadata and "Instructions to Reps" are stored on Arweave (permanent) or Ceramic (mutable streams), ensuring data cannot be deleted by any central authority.

## 4. No-Custody Principles
- Private keys never leave the user's hardware wallet or secure enclave.
- Authentication is handled via **SIWE (Sign-In with Ethereum)**, creating a stateless session.
