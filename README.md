# WeTheGoverned – Decentralized Voter Platform

> Forked from [PrimalHQ/primal-android-app](https://github.com/PrimalHQ/primal-android-app) and repurposed for true civic representation in U.S. congressional districts.

[![Kotlin](https://img.shields.io/badge/kotlin-000000?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android Studio](https://img.shields.io/badge/androidstudio-000000?style=for-the-badge&logo=androidstudio&logoColor=white)](https://developer.android.com/studio)
[![Nostr](https://img.shields.io/badge/nostr-powered-purple?style=for-the-badge)](https://nostr.com)
[![MIT License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)](LICENSE)

---

## What is WeTheGoverned?

WeTheGoverned repurposes Primal's battle-tested Nostr Android client into a
**censorship-resistant civic engagement platform** scoped to U.S. congressional
districts.  Rather than a social feed of posts, the app surfaces:

| Feature | Description |
|---|---|
| 🗳️ **District Polls** | Real-time resident voting on local issues, scoped to your district |
| 📊 **Representative Scorecard** | Official data vs. resident-reported metrics, graded A–F |
| 📋 **Candidate Manifestos** | Platform statements + public Q&A threads |
| 📈 **District Metrics** | Side-by-side comparison of official and resident-reported data |
| 🔐 **Tiered Verification** | Tier 1 (email) → Tier 2 (address) → Tier 3 (identity) |

Beta launch: **Florida District 6 (FL-06)**

---

## How the Primal Codebase Was Repurposed

The original Primal app is a clean-architecture Nostr client.  Every layer was
mapped to a civic equivalent:

### Architecture Map

| Primal Layer / Component | WeTheGoverned Equivalent |
|---|---|
| `NostrEvent` (domain model) | `CivicEvent` with civic-specific `kind` values (30100–30500) |
| `FeedRepository` | `PollRepository` – district-scoped poll feed |
| `ProfileRepository` | `ResidentRepository` – verified resident profiles |
| `EventInteractionRepositoryImpl.likeEvent()` | `PollRepositoryImpl.vote()` (same optimistic-update pattern) |
| `EventInteractionRepositoryImpl.zapEvent()` | `ScorecardRepositoryImpl.submitMetricReport()` |
| `PostData` Room entity | `DistrictPollEntity`, `RepresentativeScorecardEntity`, etc. |
| `PostDao` | `PollDao`, `ScorecardDao`, `ManifestoDao`, `MetricDao` |
| `FeedScreen` (Compose) | `HomeScreen` with `PollFeed` |
| `FeedViewModel` | `HomeViewModel` |
| `PrimalPublisher` (relay broadcast) | `CivicPublisher` → same relay infra, new relay URL |
| Long-form article / Reads | `CandidateManifesto` + `ManifestoQuestion` Q&A |

### What Was *Not* Changed

- Core WebSocket networking (`PrimalApiClient` / Ktor)
- NIP-01 event signing (key management, `keyStore`)
- Room database patterns (DAOs, TypeConverters, migrations)
- Hilt dependency injection wiring
- Jetpack Compose theming infrastructure
- `DispatcherProvider` / coroutine patterns
- `build.gradle.kts` / Gradle multi-module layout

### New Custom Nostr Event Kinds

```
30_100  DISTRICT_POLL         – parameterised replaceable poll definition
30_101  POLL_VOTE             – a resident's signed vote
30_200  REPRESENTATIVE_SCORE  – official scorecard snapshot
30_300  MANIFESTO             – candidate platform document
30_400  METRIC_REPORT         – resident-submitted district metric
30_500  RESIDENT_PROFILE      – tiered verification profile
```

All kinds fall in NIP-78's `30000–39999` parameterised replaceable event range.

---

## Module Structure

```
primal-android-app/
├── domain/          # Pure Kotlin: CivicEvent, DistrictPoll, ResidentProfile…
│                    # (replaces: NostrEvent, Profile, Stream)
├── data/
│   ├── local/       # Room entities + DAOs for civic data
│   │                # (replaces: PostData, ProfileData, PostDao…)
│   ├── remote/      # CivicApi – fetches official data + relay events
│   └── repository/  # PollRepositoryImpl, ScorecardRepositoryImpl…
├── core/            # CivicPublisher, DispatcherProvider (relay URL swapped)
├── app/             # Jetpack Compose UI screens + ViewModels
│   └── ui/
│       ├── home/         # HomeScreen + HomeViewModel (replaces FeedScreen)
│       ├── poll/         # PollDetailScreen + vote flow
│       ├── scorecard/    # ScorecardScreen (new)
│       ├── district/     # MetricsScreen (new)
│       └── profile/      # ResidentProfileScreen (replaces ProfileScreen)
└── shared/          # Shared Kotlin Multiplatform code (unchanged)
```

---

## Getting Started

### Prerequisites

- Java 17
- Android SDK
- Android Studio (Hedgehog 2023.1.1 / AGP 8.2)
- Android 8.0+ device or emulator

### Build

```bash
git clone https://github.com/wethegoverned1776-lab/primal-android-app
cd primal-android-app
./gradlew assembleDebug
```

### Install (debug)

```bash
./gradlew installDebug
```

### Release Build

Create `config.properties` in the project root:

```properties
localStorage.keyAlias=<YourKeystoreAlias>
playStore.storeFile=<PathToCert>
playStore.storePassword=<CertPassword>
playStore.keyAlias=<YourAlias>
playStore.keyPassword=<AliasPassword>
```

Then:

```bash
./gradlew installAospAltRelease
```

---

## Resident Verification Tiers

| Tier | Requirement | Permissions |
|---|---|---|
| **Tier 1** | Email verified | Browse polls, read manifestos |
| **Tier 2** | Address verified (in district) | Vote on polls, ask Q&A questions |
| **Tier 3** | Identity verified | Submit district metric reports |

---

## Relay

WeTheGoverned events are broadcast to:

```
wss://relay.wetheGoverned.net
```

Fallback (dev/testing):

```
wss://relay.damus.io
```

---

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting a pull request.

Key areas needing contribution:

1. `CivicApi` remote implementation (Congress.gov + OpenStates API integration)
2. `WsCivicPublisher` relay broadcast (delegate to `PrimalApiClient`)
3. Tier 2/3 verification backend + proof token flow
4. `PollDetailScreen` full vote UI
5. `MetricsScreen` resident report submission form
6. District onboarding flow (geo-based district detection)

---

## License

MIT – see [LICENSE](LICENSE).  Upstream Primal code © PrimalHQ contributors.
