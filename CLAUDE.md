# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Location Alarm (위치 알람) — an Android app that triggers notifications when the user enters or exits a geofenced area. Users create "routines" with a target location, radius, direction (enter/exit), and day-of-week schedule. The app uses Naver Maps for map display and place search, Google Play Services for geofencing and fused location, and Room for local persistence.

**Package:** `com.jdm.alarmlocation`
**Min SDK:** 31 (Android 12) | **Target/Compile SDK:** 36
**Language:** Kotlin | **UI:** Mixed — Jetpack Compose (most screens) + XML/DataBinding (Naver Map screen and legacy screens)

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires Key.jks and local.properties secrets)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew testDebugUnitTest --tests "com.jdm.alarmlocation.ExampleUnitTest"
```

## Architecture

Clean Architecture with three layers, all in a single `:app` module:

### Layer Structure (`app/src/main/java/com/jdm/alarmlocation/`)

- **domain/** — Models (`Alarm`, `Routine`, `Place`, `NameLocation`), repository interfaces (`AlarmRepository`, `SearchRepository`), and `Mapper.kt` (extension functions for entity↔domain conversion)
- **data/** — Room database (`AppDatabase` with `LocationDao`, `AlarmDao`, `RoutineDao`), Retrofit APIs (`SearchApi` for Naver Search, `ReverseGeoApi` for NCP reverse geocoding), repository implementations, Hilt DI modules (`di/`)
- **presentation/** — Activities, ViewModels, dialogs, custom views, services, and utilities

### Key Patterns

- **DI:** Hilt (`@HiltAndroidApp` on `AlarmApp`). Modules in `data/di/`: `NetworkModule` (dual Retrofit instances with `@SEARCH`/`@NCP` qualifiers), `DatabaseModule`, `RepositoryModule`, `UtilModule`
- **Base classes:** `BaseActivity<T: ViewDataBinding>` (template method: `initView()` → `initEvent()` → `subscribe()` → `initData()`), `BaseViewModel`, `BaseDialogFragment`, `BaseBottomSheetDialogFragment`
- **Navigation:** Activity-based with `ActivityResultContracts` launchers. Slide animations in `res/anim/`
- **State:** `SingleLiveEvent` for one-shot UI events, LiveData for list data

### Core Domain

- **Routine** — a recurring alarm group (on/off toggle, contains multiple `Alarm` entries for different days)
- **Alarm** — a single day's alarm tied to a routine (location, radius, enter/exit, day-of-week, time)
- Geofencing via `GeofenceHelper` (wraps `GeofencingClient`) and `GeofenceBroadcastReceiver`
- `FusedLocationService` — foreground service for continuous location tracking as fallback
- `MidnightReceiver` — daily broadcast to refresh geofences at midnight

### External Services

- **Naver Maps SDK** — map display, initialized in `AlarmApp` with NCP client key
- **Naver Search API** — place search (`openapi.naver.com`)
- **NCP Reverse Geocode API** — coordinates to address (`maps.apigw.ntruss.com`)
- **Firebase** — Analytics, Remote Config, Performance, Cloud Messaging

## Secrets / Local Properties

API keys are read from `local.properties` via `gradleLocalProperties()` and exposed as `BuildConfig` fields:

- `cliendid` → `NAVER_CLIEND_ID` (Naver Maps NCP key)
- `searchCliendId` → `SEARCH_CLIENT_ID` (Naver Search)
- `searchCliendSecret` → `SEARCH_SECRET_ID` (Naver Search)
- `ncpapikey` → `NCP_API_ID` (NCP Reverse Geocode)
- `cliendSecret` → `NCP_API_SECRET_ID` (NCP Reverse Geocode)
- `keyPassword`, `storePassword` (release signing)

## Coordinate Convention

Naver Search API returns coordinates multiplied by 10,000,000 (as strings). Conversion functions in `domain/Mapper.kt` handle `Place ↔ LatLng` transforms with this scaling factor.

## UI Architecture (redesign in progress)

The app is being rebuilt to the 4 spec docs (FEATURE.MD, REQUIREMENTS_SPEC.md, USER_SCENARIOS.md, DESIGN_SPEC.md). Two UI stacks coexist by deliberate rule:

- **Compose + MVI** for all non-map screens. Base contracts in `presentation/ui/compose/base/` (`UiState`/`UiIntent`/`UiEffect` markers, `MviViewModel` with `state: StateFlow` + one-shot `effect: Flow`, `CollectEffect`). Each screen is a `*Contract.kt` (State/Intent/Effect) + `*ViewModel.kt` (`@HiltViewModel` extending `MviViewModel`) + `*Screen.kt`.
- **XML + MVVM** for the Naver Map screen only (`presentation/ui/location/SearchLocationActivity` — map + radius + enter/exit picker), since the Naver Maps SDK has no Compose API. Legacy XML/MVVM screens (`MainActivity`, `CreateRoutineActivity`, `TimeActivity`, `SplashActivity`) remain registered but are being phased out by the Compose flow.

**Entry point:** `presentation/ui/compose/ComposeMainActivity` (the manifest LAUNCHER). It hosts the Compose `AppNavHost` and bridges to the XML map screen via `ActivityResultContracts.StartActivityForResult` (for both create and edit), writing the picked location into `RoutineDraftStore` before navigating to the Compose create step.

**Splash / version gate:** the nav start destination is `splash/` (Compose+MVI). It calls `AppConfigRepository.checkVersion(BuildConfig.VERSION_NAME)` (Firebase Remote Config impl `AppConfigRepositoryImpl`) → FORCE/OPTIONAL/UP_TO_DATE → force-exit dialog / optional dialog / proceed. On proceed it routes by `AuthRepository.user` (logged in → list, else login). This replaces the legacy `SplashActivity` version-check, which is now unused by the Compose flow.

**Compose packages** (`presentation/ui/compose/`): `theme/` (design tokens from DESIGN_SPEC — `AppTheme.colors/typography/shapes`), `base/`, `components/` (shared `AppBottomNav` with `BottomTab` enum), `navigation/` (`Routes`, `AppNavHost`, `MainTabsScreen`), and feature packages `splash/`, `login/`, `onboarding/`, `list/`, `create/`, `detail/`, `charge/`, `profile/`, `alarm/`.

**Bottom-nav tabs:** 목록/충전/마이 are unified under one `MainTabsScreen` (route `main`, a `Scaffold` with shared `AppBottomNav`) — tab switching is internal state, no route change. Create/detail are pushed over `main`; on save/delete they `popBackStack(MAIN)`. `ChargeScreen(onBack = null)` renders without a back button in tab mode. `ProfileScreen` (마이) shows the auth user's name + `BuildConfig.VERSION_NAME` + logout (→ clears back stack to login).

**Screens implemented:** 01 login, 02 permission onboarding, 03 list (+ D1 ticket-consume notice, D2 insufficient sheet), 04-2/04-3 create time/notification (+ D8 time validation), 04-4 detail + 04-5 delete, 05 ticket charge (+ D5 ad limit, D6 purchase verifying), 06 full-screen alarm (`alarm/AlarmFullScreenActivity`, standalone, shows-when-locked; consumes a ticket on trigger and auto-OFFs the routine at 0 → D4). Edit reuses the create flow via `onStartEdit`.

**New domain/data:** `LocationRoutine` (+ `AlarmDirection`, `AlarmMethod`), `AuthUser`, `TicketState`, `Version`/`VersionStatus`; repository interfaces `RoutineRepository`, `AuthRepository`, `TicketRepository`, `AppConfigRepository`. Auth/ticket/routine impls are **local stubs** (`*Stub.kt`, in-memory) — Kakao/Firebase auth, AdMob, Play Billing, and the backend are not integrated. `AppConfigRepositoryImpl` is real (Firebase Remote Config). All bound in `data/di/RepositoryModule`.

**Create flow bridge:** `data/draft/RoutineDraftStore` (`@Singleton`, in-memory) carries the in-progress routine between the XML location step and the Compose time/notification step (used for both create and edit).

**Geofence → alarm pipeline (`presentation/service/routine/`):** new-model geofencing lives here, separate from the legacy `service/GeofenceBroadcastReceiver` (which is unused by the Compose flow). `RoutineGeofenceManager` (`@Singleton`, started in `AlarmApp.onCreate`) subscribes to `RoutineRepository.routines` and registers/removes geofences (requestId `lr_<id>`) for ON routines scheduled today — so toggle/create/edit/delete auto-reconcile. `RoutineGeofenceReceiver` handles ENTER/EXIT → checks direction → day → time range → cooldown (`AlarmCooldownStore`: 24h when no time range, once-per-day within range) → consumes 1 ticket → dispatches via `AlarmNotifier` (ALARM = full-screen-intent notification → `AlarmFullScreenActivity`; PUSH = normal notification) → auto-OFFs the routine at 0 tickets. `AlarmFullScreenActivity`/VM are now pure display (data via intent extras); the receiver is the single point of ticket consumption. `MidnightReceiver` also calls `RoutineGeofenceManager.syncNow()` for the weekday rollover. Requires location + background-location + notification permissions to actually deliver.

**Not yet built (next phase):** real SDK integrations (Kakao/Firebase auth, AdMob, Play Billing) + backend; D3 background-permission dialog (E-1) on the list (the warn strip is shown, but tapping it doesn't yet open settings); live NaverMap preview in detail (currently a location banner); persistence (stubs are in-memory, so geofences reset on process death until Room-backed); snooze ("5분 뒤 다시 알림") currently just dismisses. `navigation/ComingSoonScreen` remains a generic placeholder.

## Project Skills

Reusable Android/Kotlin skills live in `.claude/skills/<skill-name>/SKILL.md` (one level deep — Claude Code does not discover skills nested inside category subfolders). Invoke a relevant skill before implementing matching work:

- **architecture** — `android-architecture`, `android-viewmodel`, `android-data-layer`
- **UI** — `compose-ui`, `compose-navigation`, `coil-compose`, `android-accessibility`
- **concurrency / networking** — `android-coroutines`, `kotlin-concurrency-expert`, `android-retrofit`
- **build / tooling** — `android-gradle-logic`, `gradle-build-performance`
- **performance** — `compose-performance-audit`
- **testing / automation** — `android-testing`, `android-emulator-skill`
- **migration** — `xml-to-compose-migration`, `rxjava-to-coroutines-migration`

Note: several skills target Jetpack Compose, while this app currently uses XML layouts with DataBinding — apply them when migrating or adding Compose, and use `xml-to-compose-migration` for that transition.

## Git Branching

- `main` — production branch
- `dev` — development branch (current)