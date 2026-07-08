# SCALING ERRORS V4 - Loop 4 of 5 (Performance & Verification)

This pass identifies performance degradations in cryptographic verification and UI rendering during mass-scale scrolling.

| ID | Platform | Component | Error/Bottleneck | Severity | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ERR_V4_01 | Shared | Cryptography| Schnorr signature verification (Nostr) is too slow for 334M events on the main sync loop. | CRITICAL | PENDING |
| ERR_V4_02 | Android/iOS| UI | Excessive recomposition when polling results update 100+ times per second in the list. | MAJOR | PENDING |
| ERR_V4_03 | Shared | Data | Full-list poll filtering in the ViewModel consumes 100% CPU when "All" scope is selected. | CRITICAL | PENDING |
| ERR_V4_04 | Shared | Network | No timeout for relay subscriptions; dead connections linger and consume memory. | MAJOR | PENDING |

---
## Fix Progress V4
- [x] Fix ERR_V4_01: Implement a parallel `VerificationDispatcher` for Schnorr signatures.
- [x] Fix ERR_V4_02: Use `derivedStateOf` and `distinctUntilChanged` for list item updates (via sampling).
- [x] Fix ERR_V4_03: Move all filtering logic into the `FileBasedRepository` (off-main-thread).
- [ ] Fix ERR_V4_04: Add heartbeat/ping logic to `NostrRelayManager`.
