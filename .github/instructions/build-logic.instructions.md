---
description: Use when editing Gradle files, module dependencies, Dokka configuration, version catalog entries, GitHub workflows, or buildSrc logic in android-emojify.
applyTo: build.gradle.kts, settings.gradle.kts, gradle/**/*.toml, gradle/**/*.properties, buildSrc/**/*.kt, */build.gradle.kts, .github/workflows/*.yml
---

# Build Logic Guidance

- Prefer the shared `io.wax911.emojify` plugin and `buildSrc` helpers over duplicating Android, Kotlin, Dokka, Spotless, publishing, or test configuration in individual modules.
- The pinned Java version is `21.0.8` (tracked in `.java-version`). The systems use `jenv` to select the active JDK; `.java-version` is read by `jenv local` locally and by `actions/setup-java` (via `java-version-file`) in CI. Keep new build logic compatible with JVM target 21.
- Add or update dependency versions in `gradle/libs.versions.toml` first, then reference the alias from module build files or `buildSrc`.
- Release version and code are managed in `gradle/version.properties` and read by `PropertiesReader` in `buildSrc`.
- Keep module dependency changes aligned with the existing graph: `:contract` is lowest, `:serializer:*` depend on `:contract`, `:emojify` depends on `:contract`, and `:app` is excluded from CI.
- Shared Android defaults come from `buildSrc/…/components/AndroidConfiguration.kt`, including SDK levels (`compileSdk = 36`, `minSdk = 21`, `targetSdk = 36`), JVM target 21, compiler opt-ins, and packaging exclusions.
- Shared dependency strategy comes from `buildSrc/…/strategy/DependencyStrategy.kt`, including Kotlin stdlib, Timber, JUnit4, and MockK.
- Shared formatting comes from `buildSrc/…/components/AndroidConfiguration.kt#configureSpotless` and the license header under `spotless/copyright.kt`.
- Shared Dokka behavior comes from `buildSrc/…/components/AndroidOptions.kt`; `reportUndocumented = true` and `.internal` packages are suppressed.
- Publishing configuration is in `buildSrc/…/components/AndroidOptions.kt`; the group is `io.wax911.emoji`.
- The sample `:app` module is included in `settings.gradle.kts` only when the `CI` environment variable is absent. Never add `:app`-only logic to shared build conventions.
- The test pipeline for `:emojify` requires three tasks in order: `emojify:preTest` (copies emoji fixture), `emojify:test`, `emojify:postTest` (cleans fixture). Do not run `emojify:test` in isolation.
- If you need a new convention across many modules, prefer adding it once in `buildSrc` instead of repeating it in each `build.gradle.kts` file.
- When validating Gradle changes locally, pair the work with the existing `jenv-gradle-low-ram` skill if JDK alignment or memory pressure becomes a problem.
