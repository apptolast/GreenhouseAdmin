# CLAUDE.md - GreenhouseAdmin

## Development Guidelines (IMPORTANT)

1. **Do NOT invent or hallucinate** - Verify with official documentation
2. **Use web search when needed** - Consult official Kotlin Multiplatform, Ktor, and Koin docs
3. **Ask if unclear** - Clarify requirements before implementing
4. **Follow established patterns** - Use MVVM architecture with repository pattern
5. **Code comments in English** - All technical comments and KDoc in English. UI strings can be in Spanish

## Project Overview

**Kotlin Multiplatform (KMP)** admin portal using **Compose Multiplatform** for managing greenhouse clients. Primary target is **Web (Wasm/JS)** with future mobile adaptation planned.

- **Package**: `com.apptolast.greenhouse.admin`
- **Architecture**: MVVM + Repository Pattern
- **Platforms**: Android, iOS, Desktop (JVM), Web (Wasm/JS) - **Web priority**

## Build Commands

### Web (Primary)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # Wasm (recommended)
./gradlew :composeApp:jsBrowserDevelopmentRun       # JS (legacy)
```

### Yarn Lock (IMPORTANT - After dependency changes)
```bash
./gradlew kotlinUpgradeYarnLock        # JS target
./gradlew kotlinWasmUpgradeYarnLock    # Wasm target
```

### Other Platforms
```bash
./gradlew :composeApp:assembleDebug    # Android APK
./gradlew :composeApp:run              # Desktop
# iOS: Open iosApp/ in Xcode
```

## Architecture

### MVVM Structure
```
composeApp/src/commonMain/kotlin/com/apptolast/greenhouse/admin/
├── presentation/
│   ├── ui/                    # Composables (Views)
│   │   └── theme/             # Colors, Typography, Theme
│   └── viewmodel/             # ViewModels
├── domain/
│   └── repository/            # Repository interfaces
├── data/
│   ├── model/                 # DTOs
│   ├── remote/
│   │   ├── api/               # API services
│   │   └── KtorClient.kt      # HTTP client config
│   └── repository/            # Repository implementations
├── di/                        # Koin modules
└── util/                      # Utilities, expect/actual
```

### Source Sets
```
composeApp/src/
├── commonMain/          # Shared code (all platforms)
├── androidMain/         # Android-specific
├── iosMain/             # iOS-specific
├── jvmMain/             # Desktop-specific
├── jsMain/              # JavaScript target
└── wasmJsMain/          # WebAssembly target
```

## Dependency Injection (Koin)

### Setup (gradle/libs.versions.toml)
```toml
[versions]
koin-bom = "4.1.1"

[libraries]
koin-bom = { module = "io.insert-koin:koin-bom", version.ref = "koin-bom" }
koin-core = { module = "io.insert-koin:koin-core" }
koin-compose = { module = "io.insert-koin:koin-compose" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel" }
```

### Module Structure
```kotlin
// di/DataModule.kt
val dataModule = module {
    single { createHttpClient() }
    singleOf(::ApiService)
    singleOf(::RepositoryImpl) bind Repository::class
}

// di/PresentationModule.kt
val presentationModule = module {
    viewModelOf(::MyViewModel)
}
```

### Usage in Composables
```kotlin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Screen() {
    val viewModel: MyViewModel = koinViewModel()
}
```

### Platform Initialization
- **Android**: `GreenhouseApplication : Application` → `initKoin { androidContext(this) }`
- **iOS**: `KoinInitializerKt.doInitKoin()` in iOSApp.swift
- **Desktop/Web**: `initKoin()` in main.kt

## Network Layer (Ktor)

### Setup (gradle/libs.versions.toml)
```toml
[versions]
ktor = "3.0.3"

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
# Platform engines
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }      # Android/JVM
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }      # iOS
ktor-client-js = { module = "io.ktor:ktor-client-js", version.ref = "ktor" }              # JS/Wasm
```

### Client Configuration
```kotlin
// data/remote/KtorClient.kt
fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.INFO }
    defaultRequest { url(Environment.current.baseUrl) }
}
```

### Environment Configuration
```kotlin
// util/Environment.kt
enum class Environment(val baseUrl: String) {
    DEV("https://api-dev.example.com"),
    PROD("https://api.example.com");

    companion object {
        val current = DEV
    }
}
```

## Expect/Actual Pattern

### When to Use
- No multiplatform library exists
- Platform-native APIs required
- Factory functions for platform implementations

### When NOT to Use
- Multiplatform library exists (kotlinx-datetime, kotlinx-coroutines)
- Interface with DI would suffice

### Example
```kotlin
// commonMain
expect fun getCurrentTimestamp(): String

// androidMain / jvmMain
actual fun getCurrentTimestamp(): String = Clock.System.now().toString()

// iosMain
actual fun getCurrentTimestamp(): String = NSISO8601DateFormatter().stringFromDate(NSDate())
```

## UI Theming

### Structure
```
presentation/ui/theme/
├── Color.kt       # Color palette (light & dark)
├── Type.kt        # Typography scale
└── Theme.kt       # GreenhouseAdminTheme
```

### Colors (Dark Theme)
```kotlin
primary = Color(0xFF00E676)           // Neon green
background = Color(0xFF0F1419)        // Almost black
surface = Color(0xFF1A1E23)           // Dark gray
```

### Usage
```kotlin
// Always use MaterialTheme - never hardcode colors
Text(
    text = "Label",
    color = MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.bodyMedium
)
```

## Internationalization

### String Resources
Located in `composeApp/src/commonMain/composeResources/values/`

```
values/
├── strings.xml           # English (default)
└── strings-es.xml        # Spanish
```

### Usage
```kotlin
import greenhouseadmin.composeapp.generated.resources.*

Text(stringResource(Res.string.welcome_message))
```

### Guidelines
- **Code & comments**: Always in English
- **Default strings**: English
- **Translations**: Spanish (primary), others as needed

## Key Dependencies

| Library               | Purpose              | Version |
|-----------------------|----------------------|---------|
| Kotlin                | Language             | 2.3.0   |
| Compose Multiplatform | UI                   | 1.9.3   |
| Koin                  | Dependency Injection | 4.1.1   |
| Ktor                  | HTTP Client          | 3.0.3   |
| Kotlinx Serialization | JSON                 | 1.8.0   |
| Lifecycle ViewModel   | MVVM                 | 2.9.6   |

## Best Practices

1. **Constructor Injection** - No field injection with Koin
2. **Interface Abstraction** - Depend on interfaces, not implementations
3. **Single Responsibility** - One purpose per class
4. **StateFlow for UI** - Use StateFlow in ViewModels, collect with `collectAsState()`
5. **Repository Pattern** - All data access through repositories
6. **Coroutines** - Use `viewModelScope` for async operations

## References

- [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Ktor Client](https://ktor.io/docs/client.html)
- [Koin](https://insert-koin.io/docs/reference/koin-mp/kmp/)
- [Material 3](https://m3.material.io/)
