---
applyTo: **
description: Use when understanding android-emojify architecture, module boundaries, Dokka documentation, consumer-facing APIs, or shared Gradle/buildSrc behavior.
---

# Android Emojify Context

- `android-emojify` is a reusable Android Kotlin library that provides emoji support. It is a port of [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java). Favor stable, consumer-facing APIs and reusable abstractions over app-specific behavior.
- The primary downstream consumer is the AniTrend application; changes should remain easy to integrate for external Android apps.
- Treat the published Dokka site as part of the product surface: `https://anitrend.github.io/android-emojify/`.
- Supported emoji list is documented in `SUPPORTED.md`.

## Module Groups

- Core library: `:emojify` — emoji parsing, management, trie lookup, Fitzpatrick modifier support
- Shared contracts: `:contract` — `IEmoji`, `IEmojiDeserializer`, trie `Matches`
- Serializers: `:serializer:kotlinx`, `:serializer:gson`, `:serializer:moshi` — JSON deserialization implementations
- Sample app: `:app` — included locally only; excluded from CI via `settings.gradle.kts`

## Dependency Direction

- `:contract` is the lowest module; it defines shared model and serializer interfaces with no project dependencies.
- `:serializer:*` modules each depend on `:contract` and provide a concrete `IEmojiDeserializer` implementation.
- `:emojify` depends on `:contract` and uses whichever serializer is injected at runtime.
- `:app` depends on `:emojify` and one or more serializers; it is excluded from CI.
- Never introduce cycles. Serializers must not depend on `:emojify`; `:emojify` must not depend on a specific serializer.

## Package Expectations

- `:emojify` exposes `EmojiManager`, `EmojiParser`, `FitzpatrickAction`, trie helpers in `util/`, and parser candidates in `parser/`.
- `:contract` exposes `model/IEmoji`, `serializer/IEmojiDeserializer`, and `util/trie/Matches`.
- `:serializer:kotlinx` exposes a kotlinx.serialization-based deserializer.
- `:serializer:gson` exposes a Gson-based deserializer.
- `:serializer:moshi` exposes a Moshi-based deserializer.

## Build And Tooling Facts

- All library modules apply the shared `io.wax911.emojify` Gradle plugin from `buildSrc` (entry point: `CorePlugin`).
- Shared Android defaults live in `buildSrc/…/components/AndroidConfiguration.kt`: `compileSdk = 36`, `minSdk = 21`, `targetSdk = 36`, JDK/JVM target 21, compiler opt-in `kotlin.ExperimentalStdlibApi`.
- The repo Java pin is `.java-version = 21.0.8`. The systems are expected to have `jenv` installed; `.java-version` is used by `jenv` to set the local Java version and by the `actions/setup-java` CI action via `java-version-file`.
- Dependency versions belong in `gradle/libs.versions.toml` before they are referenced from module build files.
- Release version and code are tracked in `gradle/version.properties`.
- Spotless and ktlint are enforced centrally via `buildSrc` on all library modules; the license header is sourced from `spotless/copyright.kt`.

## Testing Facts

- Unit tests live in `emojify/src/test/kotlin` using JUnit4 and MockK.
- Emoji fixture (`emoji.json`) must be copied into test resources via `./gradlew emojify:preTest` before running tests; clean up with `./gradlew emojify:postTest`.
- A static emoji test reference lives in `emojify/src/test/resources/io/wax911/emojify/core/emoji-test.txt`.
- The test pipeline must run in order: `emojify:preTest` → `emojify:test` → `emojify:postTest`.

## Documentation Contract

- Dokka is configured in `buildSrc/…/components/AndroidOptions.kt`; CI publishes `./gradlew dokkaHtmlMultiModule` output from `dokka-docs/` to the `docs` branch.
- `reportUndocumented = true`, so undocumented public APIs are a quality problem.
- Packages matching `.*\.internal.*` are suppressed from published docs. Keep consumer-facing APIs in documented public packages.
- Update KDoc whenever public behavior changes.

## Working Heuristics

- Put new serializer logic in the appropriate `:serializer:*` module, not in `:emojify`.
- When adding or changing emoji data, update `emojify/src/main/assets/emoticons/emoji.json` and verify via `scripts/emoji_generator`; document source versions in the PR.
- Verify parity across all three serializers (`kotlinx`, `gson`, `moshi`) whenever the emoji schema changes.
- Prefer shared build logic changes in `buildSrc` over duplicating Gradle configuration in individual modules.
- When running Gradle locally with memory constraints, use the `jenv-gradle-low-ram` skill.