# Contributing to WeTheGoverned

Thank you for helping build a decentralized platform for civic representation.  
This guide covers how to contribute code, data integrations, and district expansions.

---

## Code of Conduct

We follow the same principles as the upstream Primal project:
- Be respectful and constructive
- Keep discussions focused on the platform and civic goals
- No partisan political content in code comments, variable names, or PR descriptions

---

## Getting Started

1. **Fork** the repository
2. **Clone** your fork: `git clone https://github.com/<you>/primal-android-app`
3. **Open** in Android Studio (Hedgehog 2023.1.1 or later)
4. **Build**: `./gradlew assembleDebug`

---

## Architecture Rules

WeTheGoverned inherits Primal's Clean Architecture. Please follow these rules:

### Domain Layer (`/domain`)
- **Zero Android dependencies** — pure Kotlin only
- All new civic models go in `net.wetheGoverned.model`
- All new repository *interfaces* go in `net.wetheGoverned.repository`
- New Nostr event kinds must be registered in `CivicEventKind`

### Data Layer (`/data`)
- Room entities mirror domain models; use `TypeConverter` for complex fields
- Each entity needs a corresponding DAO
- Repository implementations must follow the optimistic-update pattern:
  1. Local Room write
  2. Relay publish via `CivicPublisher`
  3. Revert on failure

### App Layer (`/app`)
- One `ViewModel` per screen
- `MutableStateFlow<UiState>` for all UI state
- Never expose `MutableStateFlow` — always expose `StateFlow`
- Use `hiltViewModel()` for injection in Compose screens

---

## Priority Areas

These are the highest-impact areas needing contributions:

### 1. `CivicApi` Implementation
**File:** `data/src/main/kotlin/net/wetheGoverned/remote/api/CivicApi.kt`

Needs real HTTP implementations connecting to:
- **Congress.gov API** (`https://api.congress.gov/v3`) for official votes/attendance
- **OpenStates API** (`https://v3.openstates.org`) for state-level data
- **Google Civic Information API** for district boundary detection
- **WeTheGoverned Nostr relay** for community events

### 2. `WsCivicPublisher` Implementation
**File:** `core/src/main/kotlin/net/wetheGoverned/core/CivicPublisher.kt`

Delegate to `PrimalApiClient` (already in `:core`) with:
- Relay URL: `wss://relay.wetheGoverned.net`
- Event kinds from `CivicEventKind`

### 3. Tier Verification Flow
**Files:** `ui/profile/ResidentProfileScreen.kt`, `ResidentRepositoryImpl`

Implement the upgrade flow:
- Tier 1: Email verification (standard OAuth / magic link)
- Tier 2: Address proof (Google Civic API district lookup + proof token)
- Tier 3: Identity verification (third-party KYC integration)

### 4. Metric Report Submission Form
**File:** `ui/district/MetricsScreen.kt`

Build the bottom sheet / dialog that lets Tier 3 residents submit district metrics.

### 5. District Onboarding
**New screen:** `ui/onboarding/DistrictOnboardingScreen.kt`

Geo-based district detection using `CivicApi.detectDistrict(lat, lng)` so new
residents are automatically assigned to their correct congressional district.

### 6. JSON Serializers
**File:** `data/src/main/kotlin/net/wetheGoverned/repository/PollRepositoryImpl.kt`

Complete `parseOptionsJson` and `serializeOptionsJson` using `kotlinx.serialization`.

---

## Adding a New District

The beta targets FL-06. To add another district:

1. Add the district ID to the `District` domain model (e.g. `"us-tx-07"`)
2. Register the district relay filter in `CivicApi`
3. Add a snapshot test for the district's `HomeScreen`
4. Open a PR titled `feat(district): add TX-07`

---

## Pull Request Checklist

- [ ] Follows Clean Architecture layer rules above
- [ ] New repository methods have unit tests (JUnit 5 + MockK)
- [ ] New Compose screens have screenshot tests (Paparazzi)
- [ ] No Android imports in `:domain` module
- [ ] Optimistic-update pattern used for any write action
- [ ] `README.md` architecture map updated if new component added
- [ ] PR description explains *which* Primal component was repurposed (or that it's new)

---

## Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Screenshot tests (Paparazzi)
./gradlew recordPaparazziDebug   # record new baselines
./gradlew verifyPaparazziDebug   # verify against baselines
```

---

## License

All contributions are licensed under MIT (same as upstream Primal).
