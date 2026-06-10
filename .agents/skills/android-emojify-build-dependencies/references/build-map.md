# Build Map

Use this map to choose the right build file before editing.

| Concern | Primary files | Notes |
| --- | --- | --- |
| Module includes | `settings.gradle.kts` | Declares all modules; `:app` is excluded when `CI` env var is set |
| Root repositories and multi-module Dokka output | `build.gradle.kts` | `dokkaHtmlMultiModule` writes to `dokka-docs/` |
| Shared plugin entry point | `buildSrc/…/plugin/CorePlugin.kt` | Applies Android/app plugin, Kotlin, Dokka, publishing, Spotless, sources, dependencies |
| Shared plugin application | `buildSrc/…/components/AndroidPlugins.kt` | Library modules get `com.android.library`, Dokka, `maven-publish`, Spotless; all get `kotlin-android` |
| Shared Android defaults | `buildSrc/…/components/AndroidConfiguration.kt` | `compileSdk = 36`, `minSdk = 21`, `targetSdk = 36`, JVM target 21, compiler opt-ins, packaging exclusions, Spotless setup |
| Shared dependency strategy | `buildSrc/…/strategy/DependencyStrategy.kt` | Kotlin stdlib, Timber, JUnit4, MockK; coroutines and lifecycle added for `:app` only |
| Shared Dokka and publishing | `buildSrc/…/components/AndroidOptions.kt` | `reportUndocumented = true`, `.internal` packages suppressed, Maven publication, sources jar, classes jar |
| Version and code properties | `gradle/version.properties` | `version` and `code` fields read by `PropertiesReader` |
| Dependency versions and aliases | `gradle/libs.versions.toml` | Add or update aliases here first |
| Module path constants | `buildSrc/…/module/Modules.kt` | `Library` enum for `:emojify`, `:contract`, `:serializer:*`; `App` enum for `:app` |
| Module path references | `buildSrc/…/Libraries.kt` | Typed references used in `dependencies {}` blocks |
| Dokka publication CI | `.github/workflows/android-ci.yml` (gradle-dokka job) | Runs on push to `develop`, generates docs, deploys to `docs` branch |
| Android CI pipeline | `.github/workflows/android-ci.yml` | wrapper-validation → spotless → unit-test → publish-artifact |
| Java version (local + CI) | `.java-version` | Pin is `21.0.8`; read by `jenv` locally and by `actions/setup-java` in CI |

## Module Dependency Snapshot

- `:contract` — lowest module; no project dependencies.
- `:serializer:kotlinx` — depends on `:contract`.
- `:serializer:gson` — depends on `:contract`.
- `:serializer:moshi` — depends on `:contract`.
- `:emojify` — depends on `:contract`; uses `:serializer:kotlinx` in test scope.
- `:app` — depends on `:emojify` and serializers; excluded from CI.

## Edit Strategy

- New library version or alias: `gradle/libs.versions.toml`.
- Cross-module convention (Android config, Dokka, Spotless, dependencies): `buildSrc`.
- One module only: that module's `build.gradle.kts`.
- Documentation generation or publish behavior: `buildSrc/…/components/AndroidOptions.kt` plus workflow file.
- New module: add to `Modules.kt`, `Libraries.kt`, and `settings.gradle.kts`.
