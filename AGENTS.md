# Repository Guidelines

## Project Structure & Module Organization
- Multi-module Gradle project; library code lives in `emojify`, shared interfaces in `contract`, and serializers in `serializer/{kotlinx,gson,moshi}`.
- Sample app under `app` is included for local debugging only (not in CI); keep feature work in library modules first.
- Kotlin sources are in `src/<variant>/kotlin`, tests in `src/test/kotlin`, and emoji assets in `emojify/src/main/assets/emoticons` (copied into tests via Gradle tasks).
- Common build logic is centralized in `buildSrc`, and formatting headers sit in `spotless/`.

## Build, Test, and Development Commands
- `./gradlew :emojify:assemble` builds the core AAR; use `:contract:assemble` or serializer variants as needed.
- `./gradlew emojify:preTest emojify:test emojify:postTest` runs unit tests with the required emoji payloads; chain the tasks exactly when running locally or in CI.
- `./gradlew spotlessCheck` (or `spotlessApply`) enforces formatting and headers.
- `./gradlew :app:installDebug` deploys the sample client when the `app` module is included outside CI.

## Coding Style & Naming Conventions
- Kotlin 2.1+ with JDK 21 target; follow standard Kotlin style defined in `.editorconfig`.
- Spotless + Ktlint guard formatting on library modules; do not edit generated headers under `spotless/`.
- Use PascalCase for classes, camelCase for functions/properties, and UPPER_SNAKE_CASE for constants; package names remain lowercase under `io.wax911.emojify`.

## Testing Guidelines
- Unit tests use JUnit4 in `emojify/src/test/kotlin`; mirror the production package, and prefer descriptive names like `functionUnderTest_expectedResult`.
- Keep emoji fixture updates in `emojify/src/main/assets/emoticons/emoji.json`; ensure new cases run via `preTest` before committing.
- Add regression coverage for new serializers under their respective modules, using mock payloads where possible.

## Commit & Pull Request Guidelines
- Follow Conventional Commits as seen in history (`fix(deps): …`, `chore(build): …`); scope with the touched module when it clarifies impact.
- Before raising a PR, run `./gradlew spotlessCheck` and the emojify test pipeline locally, and attach outputs for failures.
- Reference linked issues, describe the emoji scenarios impacted, and include sample app screenshots/GIFs when UI behavior changes.
- Small, focused PRs merge faster; prefer follow-up issues for unrelated refactors or bulk dependency bumps.

## Emoji Data & Tooling Notes
- Use `scripts/emoji_generator` when updating bundled datasets; document source versions in the PR description.
- Verify serializer parity (`kotlinx`, `gson`, `moshi`) whenever schema changes land, and update README badges if release artifacts move.
