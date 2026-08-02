# Compose Compatibility Spike Decision Record

**Status:** Implemented with Tier 1 (Phase 4 of `docs/catalog-compose-migration-roadmap.md`).
**Scope:** Spike decisions only. This document records verified build choices, it does not
expand Tier 1 scope. Tier 2 items from `docs/sample-app-compose-ui-guidelines.md` section 2
are not addressed here.

---

## 1. Verified toolchain baseline

| Item | Value | Evidence |
|------|-------|----------|
| AGP | 9.3.1 | `gradle/libs.versions.toml` `gradle-plugin` |
| Gradle | 9.6.1 | `gradle/wrapper/gradle-wrapper.properties` |
| Kotlin | 2.4.10 | `gradle/libs.versions.toml` `jetbrains-kotlin` |
| JDK | 21.0.8 | `.java-version` |
| compileSdk / targetSdk | 37 | `buildSrc` `AndroidConfiguration.configureAndroid` |
| minSdk | 23 | same |
| `android.builtInKotlin` | false | `gradle.properties` (pre-existing) |
| `android.newDsl` | false | `gradle.properties` (pre-existing since commit `61b661c`, not re-added) |

No repository-wide Kotlin or AGP migration was made. `:emojify`, `:contract`, and
`:serializer:*` builds are untouched; `git diff` on those directories is empty.

## 2. Compose compiler plugin

- Alias `compose-compiler` (`org.jetbrains.kotlin.plugin.compose`) added to the shared
  catalog, versioned by `version.ref = jetbrains-kotlin` so the plugin and the Kotlin
  toolchain stay synchronized at 2.4.10.
- The plugin jar is on the `buildSrc` classpath (`libs.jetbrains.compose.compiler.gradle`)
  and is applied only to `:app` inside `buildSrc` `AndroidPlugins.configurePlugins`,
  after `kotlin-android`. Library modules never apply it.
- `buildFeatures.compose = true` is enabled for `:app` in
  `AndroidConfiguration.applyAdditionalConfiguration` (the existing `isSampleModule()`
  branch). The `viewBinding = true` toggle was removed in the same patch that deleted the
  last XML layout and the `ActivityMainBinding` reference.

## 3. Compose dependency versions (sample catalog only)

| Alias | Version | Note |
|-------|---------|------|
| `androidx-compose-bom` | 2026.06.01 | maps material3 to 1.4.0 and ui to 1.11.4 |
| `androidx-activity-compose` | 1.13.0 | |
| material3 / ui / ui-graphics / ui-tooling(-preview) | BOM-managed | versionless below the BOM |
| `ui-test-junit4`, `ui-test-manifest` | BOM-managed | instrumented test deps |

All Compose aliases live in `gradle/sample.versions.toml` (`sampleLibs`), preserving the
Phase 1 catalog ownership split. `androidx-startup-runtime` stays in the shared catalog.

Two verified resolution facts:

- The BOM must also be declared on `androidTestImplementation` (`androidTestImplementation(platform(...))`), because `androidTest` does not extend `implementation`, so the versionless `ui-test-junit4` would otherwise resolve to an empty version.
- material3 1.4.0 removed `SmallTopAppBar`; the single-row small app bar is now the base
  `TopAppBar` composable. The designer intent (small single-row app bar with the app
  title) is preserved via `TopAppBar(title = ...)`.

## 4. Activity contract: Option B, `ComponentActivity`

- `MainActivity` extends `ComponentActivity` and hosts Compose via
  `androidx.activity.compose.setContent` (activity-compose).
- `ContextExt.emojiManager()` receiver changed from `FragmentActivity` to
  `ComponentActivity`. Impact analysis before the edit: one direct caller (`MainActivity`),
  LOW risk. No new manager accessor was introduced; the `App.emojiManager` contract is
  unchanged.
- The manifest theme moved from the AppCompat `@style/AppTheme` to the platform
  `@android:style/Theme.Material.Light.NoActionBar`; `Theme.AppCompat` styles and the
  AppCompat activity dependency are gone.
- `EmojiInitializer`, the manifest startup provider, `App`, and the Kotlinx serializer are
  unchanged.
- `android:windowSoftInputMode="adjustResize"` was added to `MainActivity` so the
  IME-inset-aware Compose layout stays usable with the keyboard open (Tier 1 acceptance
  requirement, not a Tier 2 enhancement).

## 5. Behavior parity decisions

- Parser mappings are exact: `Emoji` -> `parseToUnicode`, `HTML` ->
  `parseToHtmlDecimal`, `Hex` -> `parseToHtmlHexadecimal`, synchronous, in-place
  replacement of the input field text.
- Empty input guard: `internal fun String?.isValidInput(): Boolean = !isNullOrBlank()` in
  `app/src/main/java/io/wax911/emojifysample/util/InputGuard.kt`. Null, empty, and
  whitespace-only input shows the toast `Enter text before converting`
  (`R.string.empty_input_feedback`) and never invokes a parser. This replaces the legacy
  null-only `Editable` check and its old wording.

## 6. Spotless coverage decision: extended to `:app`

- The `com.diffplug.spotless` plugin is now applied to the sample module and
  `configureSpotless()` no longer skips it, so `spotlessCheck`/`spotlessApply` cover
  sample Kotlin locally. CI is unaffected: the `spotless` job runs with `CI: true`, where
  `:app` is not included in settings.
- Target widened from `**/kotlin/**/*.kt` to also include `**/java/**/*.kt` because the
  sample keeps its sources in `src/main/java`. No library Kotlin file lives under a
  `java/` directory, so library formatting is unaffected.
- `**/androidTest/**/*.kt` was added to `targetExclude` for symmetry with the existing
  `**/test/**/*.kt` exclusion; no library module has androidTest sources.
- ktlint 1.0.1 flags `@Composable` functions named with an uppercase letter (its
  `function-naming` rule ignores no annotations by default, and this ktlint version has
  no `composable-function-naming` rule). The sample-only override
  `editorConfigOverride("ktlint_function_naming_ignore_when_annotated_with" to "Composable")`
  is set in `buildSrc` `configureSpotless`; the spotless ktlint step does not read custom
  `.editorconfig` properties in this setup, so `.editorconfig` was left unchanged.
- All `:app` main sources now carry the `spotless/copyright.kt` license header.

## 7. Tests (local-only)

- JVM: `app/src/test/kotlin/io/wax911/emojifysample/util/InputGuardTest.kt` (JUnit4)
  covers null, empty, whitespace-only, and valid input for `isValidInput()`.
- Compose UI: `app/src/androidTest/kotlin/io/wax911/emojifysample/MainScreenTest.kt`
  (instrumented, `createAndroidComposeRule<MainActivity>`) verifies the three conversion
  actions round-trip `😄` with `&#128516;` / `&#x1f604;` and that blank/cleared input
  leaves the field unchanged. Assertions target the `EditableText` semantics property
  only, because the merged `Text` list also carries the label and placeholder. Instrumentation
  runs locally only; CI continues to exclude `:app`. Verified green on an API 36 emulator
  (`:app:connectedDebugAndroidTest`, 5/5 tests).

## 8. Removed legacy assets

- `res/layout/activity_main.xml`, `res/layout/content_main.xml`
- `res/values/styles.xml`, `res/values-v23/styles.xml`, `res/values/colors.xml`
- Unreferenced `drawable-*/ic_send_grey_600_24dp.png` files
- `app/build.gradle.kts` no longer depends on `sampleLibs.google.android.material` or
  `sampleLibs.androidx.constraintlayout` (aliases stay in the sample catalog for Phase 1
  ownership; both became unused with the XML layouts)
- Kept: `mipmap-*` launcher icons, `strings.xml` (app label plus new Tier 1 copy),
  `App.kt`, `EmojiInitializer.kt`, and the startup provider.

## 9. Validation commands

All passed on `feature/catalog-compose-migration` with the effective baseline:

```
./gradlew :emojify:assemble
./gradlew spotlessCheck
env -u CI ./gradlew :app:assembleDebug
env -u CI ./gradlew :app:testDebugUnitTest
env -u CI ./gradlew :app:assembleDebugAndroidTest
env -u CI ./gradlew :app:connectedDebugAndroidTest (API 36 emulator, 5/5 green)
```

---

*End of decision record.*
