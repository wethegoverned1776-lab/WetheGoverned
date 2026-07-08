# SCALING SIMULATION V3 & ARCHITECTURAL RECOMMENDATIONS

This final simulation pass looks beyond "errors" and identifies "Architectural Risks" for a national-scale deployment.

| ID | Category | Risk / Limitation | Recommendation |
| :--- | :--- | :--- | :--- |
| REC_01 | Data | JSON file storage will suffer from OS file handle limits and slow seeks at 334M files. | Migrate to **SQLDelight** or **Room KMP** with SQLite's FTS5 for efficient full-text searching. |
| REC_02 | Security | Storing private keys in `Preferences` or raw `File` is a major security risk for 334M users. | Use **Biometric-backed Keychain (iOS)** and **EncryptedSharedPreferences/Keystore (Android)**. |
| REC_03 | Sync | Nostr Relays are not guaranteed to store all events; history might be lost. | Implement **Local-First persistence** with a dedicated "Archiver Node" strategy for verifiable history. |
| REC_04 | Network | Global Relays might censor or rate-limit a national governance app. | Deploy **Private, Permissioned Relays** alongside public ones to ensure high-availability. |
| REC_05 | UI | Displaying 334M user activity requires highly optimized "Shimmer" loading and image caching. | Integrate **Coil (KMP)** for async image loading and specialized Paging 3.0 integration. |

---
## V3 Simulated Fixes (Hardening Pass)
- [ ] Fix REC_01: Introduce a Database Abstraction layer for SQLDelight migration.
- [ ] Fix REC_02: Implement an "Identity Guard" that uses platform-secure storage.
- [ ] Fix REC_03: Add a "Verification Heartbeat" to verify local data integrity against relay hash.
