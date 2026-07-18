# JetPackComposeBaseProject16072026

A Jetpack Compose starter project following **Google's recommended app architecture**
(https://developer.android.com/topic/architecture), with Hilt, Retrofit, Room, and
Firebase already wired together end-to-end through a sample Login → Users flow.

Package: `com.nyvoratech.composebase`

## Getting started

1. Open the project in Android Studio (Koala/Ladybug or newer, AGP 8.6, Kotlin 2.0).
2. Add your real `app/google-services.json` — see `app/google-services.json.README.txt`.
3. Replace `BASE_URL` in `core/network/NetworkModule.kt` with your real API base URL.
4. Sync Gradle and run.

## Architecture overview

```
presentation (UI)  →  domain (business logic)  →  data (repositories, sources)
     ↑                                                    ↓
     └────────────────── single source of truth (Room) ───┘
```

Layers depend **inward only**. The `domain` package has zero Android/Firebase/Retrofit/Room
imports — it's pure Kotlin, which is what makes UseCases trivially unit-testable.

```
core/
  common/      Resource<T> result wrapper, AnalyticsLogger (Firebase Analytics facade)
  network/     Retrofit/OkHttp setup, ApiService, AuthInterceptor, TokenProvider
  database/    Room database, DAO, Entity
  firebase/    FirebaseAuth / Firestore / Analytics instance providers
  di/          Binds domain interfaces -> data implementations (RepositoryModule)
  ui/theme/    Material3 theme (Color/Type/Theme)
  navigation/  Type-safe Navigation Compose graph, Screen routes

domain/
  model/       Pure Kotlin models (e.g. User) — no framework imports
  repository/  Interfaces only (UserRepository, AuthRepository)
  usecase/     One class per business action (GetUsersUseCase, LoginUseCase, ...)

data/
  remote/dto/  Retrofit wire-format DTOs (UserDto)
  repository/  Interface implementations combining Room + Retrofit/Firebase
  mapper/      DTO <-> Entity <-> Domain model mapping functions

feature/
  login/       LoginScreen (stateless content + stateful host), LoginViewModel, LoginUiState
  users/       UsersScreen, UsersViewModel, UsersUiState
  session/     SharedSessionViewModel — scoped to the nav graph, shared across screens
```

## Key patterns demonstrated

- **Single source of truth**: `UserRepositoryImpl` always serves the UI from Room;
  `refreshUsers()` writes fresh network data into Room, and the UI's `Flow` updates
  automatically. Swap in a Firestore-backed repository the same way without touching
  `domain` or `feature`.
- **Sealed `Resource<T>`** (`core/common/Resource.kt`) standardizes loading/success/error
  across every repository call, instead of throwing raw exceptions up to the UI.
- **StateFlow-based `UiState`**: Each screen exposes a single immutable `data class UiState`
  via `StateFlow`, following Unidirectional Data Flow (UI reads state, calls ViewModel
  functions, state updates, UI recomposes).
- **One-off events via Channel**: Navigation triggers (`LoginEvent.NavigateToUsers`) go
  through a `Channel`, not the state object, so rotation/recomposition never re-fires them.
- **Shared ViewModel across screens**: `SharedSessionViewModel` is requested with
  `hiltViewModel(parentEntry)` where `parentEntry` is the *nested nav graph's* back stack
  entry (`Screen.SessionGraph`) rather than the individual screen's entry — see
  `ComposeBaseNavGraph.kt`. Both `LoginScreen` and `UsersScreen` resolve to the same
  instance as long as they're inside that graph.
- **DI boundaries**: `RepositoryModule` is the only file that binds a domain interface to
  a concrete implementation — this is the seam you touch when swapping backends.
- **Testability**: `LoginViewModelTest` / `UsersViewModelTest` use hand-written fake
  repositories + Turbine, with zero Android framework or real Firebase/Retrofit
  dependencies, so they run fast as plain JVM unit tests.

## Adding a new feature

1. **Domain**: add a model (if needed), a repository interface method, and a UseCase.
2. **Data**: implement the repository method (Retrofit call, Room query, Firestore call,
   or a combination), add mapper functions if you introduced a new DTO/Entity.
3. **DI**: bind any new repository interface in `RepositoryModule`.
4. **Feature**: create `feature/yourfeature/` with `YourUiState.kt`, `YourViewModel.kt`
   (annotated `@HiltViewModel`), and `YourScreen.kt` (stateful host + stateless content).
5. **Navigation**: add a `Screen` route and a `composable<Screen.YourScreen> { ... }` block.
6. **Tests**: write a fake repository in `src/test/.../fakes/` and a ViewModel test using
   Turbine for Flow/Channel assertions.

## Single-module vs multi-module

This starter is intentionally **single-module** with package-level separation (`core`,
`domain`, `data`, `feature`) — simplest to navigate for a new project. If/when build
times or team size justify it, split along these same package boundaries into Gradle
modules: `:core:common`, `:core:network`, `:core:database`, `:core:ui`, `:feature:login`,
`:feature:users`, etc. The import structure already respects those boundaries (domain
never imports data or feature code), so the split is mechanical rather than a redesign.

## Notable deliberate simplifications

- `NetworkModule`'s `BASE_URL` is a placeholder — replace before running against a real API.
- `getUserById` in `UserRepositoryImpl` does a naive cache-then-fetch; add TTL/staleness
  logic if you need it.
- Google Sign-In is not wired up (only email/password) — add the Credential Manager /
  One Tap flow into `AuthRepositoryImpl` when needed.
- No offline write-queue/sync — this template covers read-heavy caching only.
