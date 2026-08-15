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
     └────────────────── single source of truth.       ───┘
```

Layers depend **inward only**. The `domain` package has zero Android/Firebase/Retrofit/Room
imports — it's pure Kotlin, which is what makes UseCases trivially unit-testable.

```
core/
  common/       Resource<T>, AppError, AnalyticsLogger, CrashReporter
  auth/         AuthState, SessionManager, TokenManager
  network/      Retrofit/OkHttp setup, ApiCallHandler, AuthInterceptor,
                NetworkMonitor, API error handling
  database/     Room database, DAO, Entity
  firebase/     FirebaseAuth / Firestore / Analytics providers
  di/           Hilt modules and repository bindings
  ui/theme/     Material 3 theme
  navigation/   Type-safe Navigation Compose graph

domain/
  model/        Pure Kotlin domain models
  repository/   Repository interfaces
  usecase/      One class per business action

data/
  remote/dto/   Retrofit API models
  repository/  Repository implementations
  mapper/       Mapping only where DTO/Entity/Domain separation is required

feature/
  login/        LoginScreen, LoginViewModel, LoginUiState
  users/        UsersScreen, UsersViewModel, UsersUiState
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

## Authentication and session management

* Firebase Authentication is used for email/password authentication.
* `SessionManager` provides a single application-level `StateFlow<AuthState>` representing `Unknown`, `Authenticated`, or `Unauthenticated` state.
* FirebaseAuth is the source of truth for authentication state rather than treating a persisted access token as proof of an active session.
* The navigation layer waits for the initial authentication state to resolve before creating the authenticated/unauthenticated navigation flow, preventing login-screen flicker for already authenticated users.
* `TokenManager` provides centralized access-token management for authenticated API requests and keeps the in-memory token synchronized with persistent token storage.
* Firebase ID tokens are obtained through Firebase Authentication rather than implementing a custom Firebase refresh-token lifecycle.

## Centralized API and network handling

* `ApiCallHandler` provides a single reusable execution path for Retrofit requests.
* Connectivity is checked before executing network requests through `NetworkMonitor`.
* Successful HTTP responses are converted into `Resource.Success`.
* Empty successful response bodies are treated as an application-level error instead of forcing a nullable success value into the domain layer.
* HTTP failures are converted into `AppError` through centralized API error parsing.
* `IOException` during request execution is handled separately from the pre-request connectivity check:
  * `NetworkMonitor` handles the case where connectivity is unavailable before the request.
  * Request-time `IOException` represents a failure during network communication and is logged for diagnostics.
* Unexpected exceptions are reported through crash reporting and converted into a safe application-level error instead of exposing raw exceptions to the UI.

## Error handling

* `Resource<T>` provides a consistent Success/Error/Loading contract between data, domain, and presentation layers.
* `AppError` represents application-level failures without leaking Retrofit, Firebase, or platform-specific exceptions into the UI layer.
* API error responses are parsed centrally through `ApiErrorParser`.
* Firebase authentication failures preserve the Firebase error code for diagnostics and user-facing error mapping.
* Raw exception messages are not used directly as user-facing messages.
* Unknown/unexpected exceptions are logged and reported while the UI receives a safe generic error.


## Adding a new feature

1. **Domain**: add a model if required, a repository interface method, and a UseCase.
2. **Data**: implement the repository method using the appropriate remote/local data source.
3. **API**: add the Retrofit endpoint and DTO only when the API contract differs from an existing model.
4. **Mapping**: introduce DTO/Entity/Domain mapping only when separate models provide a real architectural benefit.
5. **DI**: bind any new repository interface in `RepositoryModule`.
6. **Feature**: create `feature/yourfeature/` with `YourUiState.kt`, `YourViewModel.kt`, and `YourScreen.kt`.
7. **State**: expose immutable `StateFlow<YourUiState>` from the ViewModel.
8. **Events**: use `Channel`/`Flow` for one-time UI events such as navigation.
9. **Navigation**: add a type-safe `Screen` route and corresponding `composable` destination.
10. **Error** handling: consume `Resource<T>` / `AppError` rather than handling Retrofit or Firebase exceptions directly in the UI.
11. **Testing**: add unit tests for the ViewModel/use case using fake repositories and Flow assertions where applicable.

## Authentication flow

```text
LoginScreen
    ↓
LoginViewModel
    ↓
LoginUseCase
    ↓
AuthRepository
    ↓
Firebase Authentication
    ↓
Firebase ID Token
    ↓
TokenManager
    ↓
TokenStorage
    ↓
AuthInterceptor
    ↓
Authenticated API request
...

FirebaseAuth
    ↓
SessionManager
    ↓
AuthState
    ↓
ComposeBaseNavGraph
    ├── Authenticated    → Users
    └── Unauthenticated  → Login

"The initial Unknown state prevents the navigation layer from making an authentication decision before Firebase has resolved the current session."


---


## Production considerations

* Authentication state is separated from token storage; a persisted token is not treated as the sole source of truth for Firebase authentication.
* Network availability and request-time network failures are handled as separate conditions.
* API and Firebase exceptions are converted into application-level errors before reaching the UI layer.
* Diagnostic exception details are kept separate from user-facing messages.
* Crash reporting is used for unexpected production failures without exposing raw exception details to users.
* API logging should be restricted or disabled for release builds when request/response bodies may contain sensitive data.
* Authentication tokens and other credentials must never be written to logs or analytics events.
* API base URLs and Firebase configuration are environment-specific and should not be hard-coded with production credentials.

**Final README direction**

Compose UI
    ↓
ViewModel
    ↓
UseCase
    ↓
Repository interface
    ↓
Repository implementation
    ↓
┌──────────────┬───────────────┐
│              │               │
Retrofit      Room          Firebase
│              │               │
└──────────────┴───────────────┘
       ↓
 Resource<T> / AppError

 