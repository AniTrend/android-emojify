# Repository Guidelines

## Project Structure & Module Organization
- Multi-module Gradle project; library code lives in `emojify`, shared interfaces in `contract`, and serializers in `serializer/{kotlinx,gson,moshi}`.
- Sample app under `app` is included for local debugging only (not in CI); keep feature work in library modules first.
- Kotlin sources are in `src/<variant>/kotlin`, tests in `src/test/kotlin`, and emoji assets in `emojify/src/main/assets/emoticons` (copied into tests via Gradle tasks).
- Common build logic is centralized in `buildSrc`, and formatting headers sit in `spotless/`.
- `:emojify` exposes `EmojiManager`, `EmojiParser`, `FitzpatrickAction`, trie helpers, and parser candidates. `:contract` exposes `model/IEmoji`, `serializer/IEmojiDeserializer`, and `util/trie/Matches`. `:serializer:*` modules each expose a corresponding `IEmojiDeserializer` implementation.

## Dependency Graph (must remain acyclic)
- `:contract` is the bottom module — shared models and serializer interfaces with no project dependencies.
- `:serializer:*` modules each depend only on `:contract`.
- `:emojify` depends on `:contract` and uses whichever serializer is injected at runtime.
- Serializers must not depend on `:emojify`. `:emojify` must not depend on a specific serializer.
- `:app` depends on `:emojify` and one or more serializers; excluded from CI via `settings.gradle.kts`.

## Environment & Toolchain
- JDK 21.0.8 (pinned in `.java-version`) managed via `jenv` locally and `actions/setup-java` in CI.
- Kotlin 2.4.0, Gradle 9.6.0, Android SDK 36 (compile/target), minSdk 21.
- `kotlin.ExperimentalStdlibApi` compiler opt-in is enabled globally across all library modules.
- Dependency versions live in `gradle/libs.versions.toml` — the single source of truth. Add/update there first, then reference by alias.
- Release version and code are tracked in `gradle/version.properties`, read by `PropertiesReader` in `buildSrc`.
- Publishing group is `io.wax911.emoji`.
- Build logic belongs in `buildSrc` (entry: `CorePlugin`). Do not duplicate Android/Kotlin/Dokka/Spotless/publish config in individual modules.
- The `:app` module is only included when `CI` env var is absent. Never add `:app`-only logic to shared build conventions.

## Build, Test, and Development Commands
- `./gradlew :emojify:assemble` builds the core AAR; use `:contract:assemble` or serializer variants as needed.
- `./gradlew emojify:preTest emojify:test emojify:postTest` — run in this exact order. `preTest` copies the emoji fixture into test resources, `postTest` cleans it. Never run `emojify:test` in isolation.
- `./gradlew spotlessCheck` (or `spotlessApply`) enforces formatting, headers, and ktlint rules across all library modules.
- `./gradlew dokkaHtmlMultiModule` generates the consumer docs site (published to `docs` branch via CI). `reportUndocumented = true` — undocumented public APIs produce warnings. Internal packages (`.*\.internal.*`) are suppressed from output.
- `./gradlew :app:installDebug` deploys the sample client when the `app` module is included outside CI.

## Coding Style & Naming Conventions
- Spotless + Ktlint guard formatting on library modules; do not edit generated headers under `spotless/`. The license header template is at `spotless/copyright.kt`.
- No wildcard imports (`ij_kotlin_packages_to_use_import_on_demand = unset` in `.editorconfig`).
- Use PascalCase for classes, camelCase for functions/properties, and UPPER_SNAKE_CASE for constants; package names remain lowercase under `io.wax911.emojify`.
- Keep KDoc consumer-facing — every new or changed public API must be documented. Write for someone outside this repo who does not know the emoji parsing internals; explain what, when, and how to integrate.
- KDoc conventions: short summary first, then `@param`, `@property`, `@return`, `@throws`, `@see`, and `@since` (only when the version is already known). Avoid placeholder KDoc that only restates the type name.
- Update KDoc in the same patch as the behavior change.

## Testing Guidelines
- Unit tests use JUnit4 and MockK in `emojify/src/test/kotlin`; mirror the production package, and prefer descriptive names like `functionUnderTest_expectedResult`.
- Emoji fixture updates go in `emojify/src/main/assets/emoticons/emoji.json`; a static test reference lives at `emojify/src/test/resources/io/wax911/emojify/core/emoji-test.txt`.
- When adding or changing serializer code, add regression coverage under the respective `serializer/*` module using mock payloads.

## Serializer Parity
- The three serializers (`kotlinx`, `gson`, `moshi`) must remain functionally equivalent. Whenever the emoji JSON schema changes, verify all three produce the same parsed emoji set.

## Commit & Pull Request Guidelines
- Base branch is `develop`. Target `develop` for all PRs.
- Follow Conventional Commits as seen in history (`fix(deps): …`, `chore(build): …`); scope with the touched module when it clarifies impact.
- Before raising a PR, run `./gradlew spotlessCheck` and the emojify test pipeline locally.
- Reference linked issues, describe the emoji scenarios impacted, and include sample app screenshots/GIFs when UI behavior changes.
- Use `scripts/emoji_generator` when updating bundled datasets; document source versions in the PR description.

## Context Maintenance
- When a change materially alters repository reality, update `AGENTS.md` and relevant skills in the same patch — don't leave guidance stale.
- Remove contradictory guidance instead of layering new instructions on top of obsolete ones.
- Audit `AGENTS.md` and skills when you change module boundaries, dependency direction, package ownership, shared build conventions, Dokka behavior, or consumer-facing APIs. Keep changes specific and low-churn.

## Further Context
- Supported emoji list is documented in `SUPPORTED.md`.
- Repo-local OpenCode skills live in `.agents/skills/` and `.github/skills/`: `android-emojify-build-dependencies`, `android-emojify-kdoc-dokka`, `android-emojify-reference-map`, `jenv-gradle-low-ram`.
