# Catalog Split and Compose Sample Migration: Implementation Roadmap

**Status:** Planning only. This document is the implementation contract for the sample version catalog split and the Jetpack Compose migration of the sample app. It is not implementation.

**Handoff inputs:** reconciled repository evidence (verified against `develop` at the time of writing) and `docs/sample-app-compose-ui-guidelines.md` (the UI handoff, authored by the designer).

**Execution rule:** implementers execute the phases below as separate PRs targeting `develop`, in the documented order, each with its own reviewer gate. This document owns no source code.

---

## 1. Goal, non-goals, ownership, dependency ordering

### 1.1 Goal

1. Split sample-only dependency aliases out of the shared catalog into a dedicated `gradle/sample.versions.toml` catalog named `sampleLibs`, without changing library module builds.
2. Migrate the sample app's single XML screen to Jetpack Compose with strict behavioral parity (Tier 1), after a compatibility spike.
3. Protect sample dependency automation (Renovate, auto-approve, Release Drafter) from misclassification while intentionally auto-merging narrowly matched sample catalog updates.
4. Keep the consumer library (`:emojify`, `:contract`, `:serializer:*`) build, tests, and published artifacts untouched by the sample migration.

### 1.2 Non-goals

- No changes to `:emojify`, `:contract`, or `:serializer:*` source, dependencies, or Gradle conventions.
- No repo-wide Kotlin or AGP migration, no flipping `android.builtInKotlin`.
- No Tier 2 sample enhancements (see designer guide section 2) until Tier 1 parity is proven and separately approved.
- No new publish pipeline or consumer API changes.
- No CI enablement of the sample app unless the Phase 3 decision explicitly opts in.

### 1.3 Named ownership

| Work | Owner | Gate |
|------|-------|------|
| Phase 1 baseline verification and decision record | `@builder`/Gradle specialist | Oracle sign-off on decisions in section 11 |
| Phase 2 catalog split | `@builder`/Gradle specialist | Reviewer gate + Phase 2 acceptance |
| Phase 3 Renovate/auto-approve/Release Drafter/CI protections | `@builder`/Gradle specialist | Reviewer gate + Phase 3 acceptance |
| Phase 4 compatibility spike | `@builder`/Gradle specialist, `@designer` consulted on the activity/theme decision | Reviewer gate + spike record approval |
| Phase 4 Tier 1 parity migration | `@fixer` (bounded by designer guide) | Reviewer gate + Tier 1 acceptance |
| Phase 5 Tier 2 enhancements | `@designer` leads, `@builder` supports | Oracle approval per item, only after Tier 1 parity |
| All phases | Reviewer + Oracle (repo owner `wax911`) | Oracle approves decisions in section 11 and Tier 2 go/no-go; reviewer approves each phase PR set against its acceptance criteria |

Before any `buildSrc` symbol edit (for example `DependencyStrategy`, `ProjectExtensions`), the `@builder` must run impact analysis per repository policy and report the blast radius to the reviewer.

### 1.4 Dependency ordering

```
Phase 1 (decisions) blocks everything
  -> Phase 2 (catalog split) blocks Phase 3 and Phase 4
    -> Phase 3 (automation protections) independent of Phase 4, but after Phase 2
    -> Phase 4 spike blocks Tier 1 (within Phase 4)
      -> Tier 1 parity
        -> Phase 5 (Tier 2) only after parity approval
```

Phase 4 must not add Compose aliases until Phase 2 has created `gradle/sample.versions.toml`. Phase 3 protections must land before any Renovate run that can open sample catalog PRs.

---

## 2. Effective build baseline and AGENTS.md drift

### 2.1 Effective baseline (verified from source)

| Item | Value | Source |
|------|-------|--------|
| compileSdk | 37 | `buildSrc/src/main/java/io/wax911/emoji/buildSrc/plugin/components/AndroidConfiguration.kt`, `configureAndroid()` |
| targetSdk | 37 | same file, `defaultConfig` |
| minSdk (global, all modules) | 23 | same file, `defaultConfig` |
| Java compatibility / JVM target | 21 / JVM_21 | same file, `compileOptions` and `KotlinJvmCompile` |
| Kotlin | 2.4.10 | `gradle/libs.versions.toml`, `jetbrains-kotlin` |
| AGP | 9.3.1 | `gradle/libs.versions.toml`, `gradle-plugin` |
| Gradle | 9.6.1 | `gradle/wrapper/gradle-wrapper.properties` |
| JDK | 21.0.8 | `.java-version` |
| `android.builtInKotlin` | false | `gradle.properties` |
| Sample-only module config (viewBinding, lint, opt-ins) | lives in `buildSrc` guarded by `isSampleModule()` | `AndroidConfiguration.kt`, `DependencyStrategy.kt` |

### 2.2 AGENTS.md drift (must be decided before implementation)

`AGENTS.md` currently states: Kotlin 2.4.0, Gradle 9.6.0, Android SDK 36 (compile/target), minSdk 21. All four are stale versus the effective baseline above. The last `AGENTS.md` update predates the Renovate bumps that moved the toolchain (for example `fix(deps): update kotlin monorepo to v2.4.10`, `fix(deps): update dependency com.android.tools.build:gradle to v9.3.1`).

A second contradiction: `AGENTS.md` says "Never add `:app`-only logic to shared build conventions", but the current `buildSrc` already contains sample-only branches (`viewBinding`, sample lint, coroutine opt-ins, sample dependency strategy). The drift decision must also reconcile this rule with the established `isSampleModule()` pattern.

**Requirement:** Phase 1 records an explicit decision to align docs to code (recommended) or code to docs. No implementation phase may proceed on a stale baseline. See decision D1 in section 11.

---

## 3. Implementation phases

### Phase 1: Baseline and decisions

**Owner:** `@builder`/Gradle specialist. **Gate:** Oracle sign-off on section 11 decisions.

1. Verify the section 2.1 baseline with the acceptance commands in section 6 on a clean checkout.
2. Record the five decisions from section 11 with Oracle sign-off.
3. Author the spike preconditions checklist (section 7) and the Tier 1 handoff briefing (section 8).
4. If D1 approves doc alignment, update `AGENTS.md` toolchain values and the `:app`-only logic rule in the same patch (per repo policy: guidance updated in the same patch as the reality change).

**Do not** start Phase 2 until D1 through D5 are recorded.

### Phase 2: Structural catalog split

**Owner:** `@builder`/Gradle specialist. **Gate:** reviewer + Phase 2 acceptance (section 6.2).

Implements section 4 exactly: `gradle/sample.versions.toml`, root `settings.gradle.kts` declaration, mirrored `buildSrc/settings.gradle.kts`, generated accessor classpath support in `buildSrc/build.gradle.kts`, `Project.sampleLibs`, `DependencyStrategy.kt` update, `app/build.gradle.kts` update, coroutine duplicate reconciliation, and the CI cache path update (config change here, behavior validation in Phase 3).

**Ordering within the phase:** create the sample toml and move aliases, then wire settings/buildSrc, then update consumers, then run acceptance. The library modules must build identically before and after (verify with `:emojify:assemble` and the test pipeline).

### Phase 3: Sample dependency automation and release automation verification

**Owner:** `@builder`/Gradle specialist. **Gate:** reviewer + Phase 3 acceptance (section 6.3).

1. Renovate package rules for the sample catalog (section 5.1), label `sample-dependencies`, `skip-changelog`, and intentional automerge on.
2. Auto-approve policy for sample PRs (section 5.2).
3. Release Drafter autolabeler adjustment and changelog exclusion verification (section 5.3).
4. Cache key/path validation after the Phase 2 config change (section 5.4).
5. Explicit local-only/UI CI decision (section 9): the sample stays out of CI by default. Sample dependency auto-merge is enabled by the owner despite that limitation and must be revisited if dependency updates expose breakage.

**Do not** merge this phase before the Renovate config validator and the Release Drafter dry-run pass.

### Phase 4: Compose compatibility spike and Tier 1 parity migration

**Owner:** spike by `@builder`/Gradle specialist with `@designer` consulted; Tier 1 by `@fixer`. **Gate:** reviewer + section 6.4 acceptance.

1. **Spike (section 7):** add Compose aliases and the sample-only Compose compiler plugin, decide the activity contract, viewBinding fate, and Spotless coverage. Deliverable: a spike decision record (new doc) plus a minimal Compose rendering proof on `:app`.
2. **Tier 1 (section 8, bounded by `docs/sample-app-compose-ui-guidelines.md`):** `@fixer` migrates the single screen to Compose with strict parity, runs the regression checklist, and removes replaced XML/ViewBinding assets.
3. Tier 2 items are explicitly out of scope for `@fixer`.

### Phase 5: Optional Tier 2 enhancements

**Owner:** `@designer` leads, `@builder` supports. **Gate:** Oracle approval per item, only after Tier 1 parity is accepted.

Each Tier 2 item from designer guide section 2 (async parsing, error surfaces, dark theme, adaptive layout, fonts, motion, quick emoji bar, copy/clear, separate output, accessibility polish) is implemented as its own PR with its own approval. No Tier 2 item is bundled into Tier 1.

---

## 4. Catalog design

### 4.1 Files and declarations

**New file `gradle/sample.versions.toml`.** Holds only sample-only library aliases and their version keys, moved from `gradle/libs.versions.toml`, never duplicated. Phase 4 adds the sample-only Compose library aliases here. The Kotlin Compose compiler plugin and its Kotlin version reference remain in the shared catalog because they are toolchain concerns that must stay synchronized with the repository Kotlin version.

**Root `settings.gradle.kts`** (named declaration, preserving the auto-derived `libs`):

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
        create("sampleLibs") {
            from(files("gradle/sample.versions.toml"))
        }
    }
}
```

Declaring `libs` explicitly preserves the existing accessor while avoiding reliance on Gradle's deprecated automatic import of `gradle/libs.versions.toml`.

**Mirrored `buildSrc/settings.gradle.kts`:**

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("sampleLibs") {
            from(files("../gradle/sample.versions.toml"))
        }
    }
}
```

**`buildSrc/build.gradle.kts`** (generated accessor classpath support, next to the existing `libs` workaround):

```kotlin
implementation(files(sampleLibs.javaClass.superclass.protectionDomain.codeSource.location))
```

**`ProjectExtensions.kt`** (mirror of the existing `libs` accessor):

```kotlin
internal val Project.sampleLibs: LibrariesForSampleLibs
    get() = extensions.getByType<LibrariesForSampleLibs>()
```

**`DependencyStrategy.kt`:** the sample-only branch (`applyLifeCycleDependencies`, `applyCoroutinesDependencies`, turbine) reads `project.sampleLibs`; the shared branches (`applyLoggingDependencies`, `applyDefaultDependencies`, `applyTestDependencies`) keep reading `project.libs`.

**`app/build.gradle.kts`:** UI aliases switch to `sampleLibs` (`sampleLibs.google.android.material`, `sampleLibs.androidx.constraintlayout`). The duplicate coroutine lines are deleted (single source is `DependencyStrategy`). `libs.androidx.startup.runtime` stays on the shared catalog (see 4.3). The `android { namespace }` block is unchanged.

### 4.2 Aliases that move (move, not copy)

From `gradle/libs.versions.toml` to `gradle/sample.versions.toml`:

- Versions: `androidx-lifecycle`, `google-android-material`, `jetbrains-kotlinx-coroutines`.
- Libraries: `google-android-material`, `androidx-constraintlayout`, `androidx-lifecycle-extensions`, `androidx-lifecycle-runtime-ktx`, `androidx-lifecycle-livedata-ktx`, `androidx-lifecycle-livedata-core-ktx`, `jetbrains-kotlinx-coroutines-core`, `jetbrains-kotlinx-coroutines-android`, `jetbrains-kotlinx-coroutines-test`, `cash-turbine`.
- Unused aliases, including paging, emoji, RecyclerView, lifecycle Compose, Jackson, and Kotlin Android Extensions entries, remain outside this split and are candidates for a separate cleanup chore. Do not move or duplicate unreferenced aliases as part of this work.

All of these are referenced only by `app/build.gradle.kts`, `DependencyStrategy` sample branches, or nothing. Verified by grep at evidence time.

### 4.3 Aliases that stay shared

`androidx-startup-runtime` stays in `libs` (explicit decision: the startup provider is a documented consumer integration path, and `EmojiInitializer` wiring stays unchanged). Also shared: `timber`, `junit`, `mockk`/`mockk-android`, the `androidx-test-*` family, `androidx-core-ktx`, `androidx-emoji*`, `androidx-recyclerview`, `gson`/`jackson-databind`/`moshi-kotlin`, `kotlinx-serialization-*`, `kotlinx-datetime`, `spotless-gradle`, `jetbrains-dokka-gradle`, and the plugin aliases used by `buildSrc` (`gradle-plugin`, `jetbrains-kotlin-gradle`, `gradle-versions`).

### 4.4 Coroutine duplicate reconciliation

Today `app/build.gradle.kts` and `DependencyStrategy.applyCoroutinesDependencies` both declare `coroutines-android` and `coroutines-core` for the sample. After the split, `DependencyStrategy` is the single declaration point (it also wires `coroutines-test` and `turbine` variants). The `app/build.gradle.kts` copies are removed. Acceptance greps for exactly one reference per coroutine alias.

### 4.5 Startup runtime and sample wiring

`app/build.gradle.kts` keeps `implementation(libs.androidx.startup.runtime)` from the shared catalog. No sample behavior changes in this phase; the phase is purely structural.

---

## 5. Renovate and release strategy

### 5.1 Renovate (`.github/renovate.json`)

Current state: `config:base`, `baseBranches: develop`, weekend schedule, global `automerge: true` with `automergeType: pr` and rebase strategy, no `packageRules`, no labels.

Required change, a `packageRules` entry for the sample catalog:

```json
{
  "matchFileNames": ["gradle/sample.versions.toml"],
  "matchManagers": ["gradle"],
  "additionalBranchPrefix": "sample-",
  "labels": ["sample-dependencies", "skip-changelog"],
  "automerge": true
}
```

- `additionalBranchPrefix: "sample-"` gives sample PRs a distinct branch shape (`renovate/sample-...`) so automation can tell them apart.
- `automerge: true` opts sample catalog updates into the repository's existing Renovate automerge flow. This is an explicit owner decision because the sample remains outside CI.
- The narrow file match and `sample-` branch prefix limit the policy to sample catalog updates. Library dependency PR behavior remains unchanged.

### 5.2 Auto-approve guard (`.github/workflows/auto-approve.yml`)

Current state: the workflow auto-approves every PR authored by `renovate[bot]`, unconditionally. The requested policy keeps that behavior for sample catalog PRs too.

Required policy: retain auto-approval for sample catalog PRs and all other Renovate PRs:

```yaml
if: github.event_name == 'workflow_dispatch' || github.actor == 'renovate[bot]'
```

### 5.3 Release Drafter and the blanket autolabeler

Current state: `.github/release-drafter-config.yml` autolabels any branch matching `/renovate\/.+/` as `dependencies`. This blanket rule would classify sample catalog PRs as consumer dependency changes and pull them into the consumer changelog and the patch version resolver. `exclude-labels: ["skip-changelog"]` is already configured.

Required outcome (mechanism order matters):

1. Renovate applies `sample-dependencies` and `skip-changelog` to sample PRs at creation (5.1). The autolabeler only adds labels, so these survive.
2. The autolabeler may gain a `sample-dependencies` rule keyed on the `renovate/sample-` branch prefix and `gradle/sample.versions.toml`. Autolabeler rules are additive, so a sample PR may also receive the blanket `dependencies` label. If the action supports and accepts negative lookahead, the blanket rule may be narrowed from `/renovate\/.+/` to `/renovate\/(?!sample-).+/`; otherwise, accept both labels and rely on `skip-changelog` as the hard exclusion guarantee. Verify the chosen behavior with the fixture check in 6.3.
3. The explicit changelog choice is exclusion: sample-only PRs must not appear in the consumer draft at all (`skip-changelog`). They are not a separate category in the consumer changelog, because the sample is not a published artifact.

### 5.4 Cache key/path validation

Current state: `.github/actions/android/action.yml` pins `cache-dependency-path: ./gradle/libs.versions.toml`. Once `gradle/sample.versions.toml` exists, dependency updates in it must invalidate the Gradle cache.

Required change (config lands with Phase 2, behavior validated in Phase 3): `cache-dependency-path: ./gradle/*.versions.toml`.

Validation: compare the cache key hash shown in the `Post Set up JDK` step output between two CI runs, one without and one with a whitespace-only change to `gradle/sample.versions.toml`, and confirm the key changes. The JDK side needs no change: `setup-java` already reads `.java-version`.

---

## 6. Acceptance criteria and commands

All `:app` commands require `CI` unset (`env -u CI ./gradlew ...`) because `settings.gradle.kts` includes `:app` only when `CI` is absent. Library commands run with the default environment.

### 6.1 Phase 1 acceptance

- Baseline verified on a clean checkout: `./gradlew --version` reports 9.6.1; `./gradlew :emojify:assemble` succeeds.
- The five decisions (section 11) are recorded with Oracle sign-off.
- `AGENTS.md` drift disposition recorded (doc alignment approved or deferred with rationale).

### 6.2 Phase 2 acceptance

- `./gradlew :emojify:assemble`
- `./gradlew emojify:preTest emojify:test emojify:postTest` in this exact order; never `emojify:test` alone
- `./gradlew spotlessCheck`
- `./gradlew :contract:assemble`
- `./gradlew :serializer:kotlinx:assemble :serializer:gson:assemble :serializer:moshi:assemble`
- `env -u CI ./gradlew :app:assembleDebug`
- `env -u CI ./gradlew :app:dependencies` resolves from `sampleLibs` without warnings; `:app:dependencyInsight` for coroutines shows a single declaration path
- Grep check: no alias exists in both `gradle/libs.versions.toml` and `gradle/sample.versions.toml`; exactly one reference per coroutine alias across `buildSrc` and `app`
- `buildSrc` compiles with the `sampleLibs` accessor workaround (any `./gradlew` task covers this)

### 6.3 Phase 3 acceptance

- `npx --yes renovate-config-validator .github/renovate.json` passes
- Release Drafter dry-run: run the `release-drafter/release-drafter` action with `dry-run: true` (or the bundled CLI) against `.github/release-drafter-config.yml`; the draft must contain no `sample-dependencies` PR
- Fixture inspection: a fixture PR carrying `sample-dependencies` and `skip-changelog` must be absent from the draft and must not change the resolved version
- Auto-approve policy: a fixture sample PR and a library Renovate PR are both auto-approved
- Cache validation from 5.4 passes
- If sample CI was opted in: the new job runs `spotlessCheck` and `env -u CI ./gradlew :app:assembleDebug` and is required for sample PRs

### 6.4 Phase 4 acceptance

Spike: decision record exists and states the activity contract, viewBinding disposition, Spotless disposition, and Compose plugin wiring; `env -u CI ./gradlew :app:assembleDebug` and `:app:installDebug` on the API 23 and current API devices both work with the proof screen.

Tier 1: the full designer guide section 1.10 checklist, plus the tests and manual matrix from section 9. Library modules unchanged (git diff on `emojify/`, `contract/`, `serializer/` is empty for the Tier 1 PR).

### 6.5 Phase 5 acceptance

Per-item Oracle approval recorded; Tier 1 regression suite still green after each Tier 2 item.

---

## 7. Compose compatibility spike requirements

The spike runs before Tier 1 and must document decisions, not just validate versions.

1. **Toolchain baseline:** AGP 9.3.1, Kotlin 2.4.10, Gradle 9.6.1, JDK 21.0.8, `android.builtInKotlin=false` (current, unchanged).
2. **Sample-only Compose compiler plugin:** apply `org.jetbrains.kotlin.plugin.compose` in `app/build.gradle.kts` only, using the plugin alias and Kotlin version from the shared catalog so both remain synchronized at 2.4.10. Because `android.builtInKotlin=false`, the plugin must be applied explicitly; do not rely on AGP built-in Kotlin. Compose library aliases belong in `sampleLibs`.
3. **Compose build feature:** enable `buildFeatures.compose = true` for the sample module in the `buildSrc` sample branch (`AndroidConfiguration.applyAdditionalConfiguration`, next to the existing `viewBinding` toggle). Do not touch library modules.
4. **viewBinding decision:** `viewBinding = true` stays during the spike. The spike records the exact commit where Tier 1 disables it, which is the same commit that removes the last XML layout and `ActivityMainBinding` reference.
5. **Spotless coverage decision:** `configureSpotless()` currently skips the sample (`if (isLibraryModule())`). The spike decides to extend Spotless to `:app` in Tier 1 (recommended) or to keep the sample excluded. Note the CI consequence: the `spotless` job runs with `CI: true`, where `:app` is not even included in settings, so CI enforcement of sample formatting only exists if Phase 3 opts into a sample CI job. Extending Spotless gives local enforcement either way.
6. **Activity contract decision:** spike documents Option A (retain `AppCompatActivity`, add Compose content through `androidx.activity.compose.setContent`, keep the `FragmentActivity.emojiManager()` extension) or Option B (migrate to `ComponentActivity`, update `ContextExt.kt` and any `FragmentActivity` dependency, drop AppCompat theme assets). Default is Option A unless the spike proves a blocker. The Tier 1 fixer must not change the activity class or manager accessor until this is recorded (designer guide section 1.2).
7. **No silent repo-wide Kotlin migration:** `android.builtInKotlin=false` stays; the Compose compiler plugin is sample-only; `:emojify`, `:contract`, `:serializer:*` Kotlin setup is untouched.

Spike deliverable: a decision record (new doc) and a minimal Compose rendering proof on `:app`. The spike PR may add Compose aliases to `gradle/sample.versions.toml` and the plugin to `app/build.gradle.kts`, and nothing else.

---

## 8. Compose Tier 1 strict parity handoff

The `@fixer` executes Tier 1 exactly as specified by `docs/sample-app-compose-ui-guidelines.md` (sections 1, 3, 4). The binding requirements, restated for the implementer:

1. **Three exact parser methods**, all extension functions on `EmojiManager` in `emojify/src/main/kotlin/io/wax911/emojify/parser/EmojiParser.kt` (lines 56, 156, 201): `parseToUnicode(input: String)`, `parseToHtmlDecimal(...)`, `parseToHtmlHexadecimal(...)`. The three buttons map 1:1 to these, as today in `MainActivity.onClick`.
2. **In-place replacement:** each conversion replaces the input field content with the converted result. No separate output area in Tier 1.
3. **Explicit empty-input feedback:** null or zero-length trimmed input shows the toast `Enter text before converting` and must not invoke any parser method. This is stricter than the current null-only `Editable` check and needs a regression test.
4. **Manager and initializer preservation:** `EmojiManager` is obtained through the existing `App.emojiManager` and the `FragmentActivity.emojiManager()` extension in `ContextExt.kt`; `EmojiInitializer` and the manifest startup provider remain unchanged. No new manager accessor.
5. **No XML/ViewBinding:** remove `activity_main.xml`, `content_main.xml`, the replaced `styles.xml` assets, and the `ActivityMainBinding` usage. Keep `colors.xml` and `strings.xml` values still used by the manifest or launcher icon.
6. **Basic accessible controls:** 48dp by 48dp touch targets, visible label and hint on the input, TalkBack labels `Convert to emoji`, `Convert to HTML`, `Convert to hexadecimal HTML`, logical focus order (designer guide section 1.8).
7. **Tests:** a Compose UI test covering the three conversions, and a regression test for null and blank input. Test matrix in section 9.
8. **Bounded scope:** Tier 2 items (designer guide section 2) are not fixer scope; the fixer must not implement any of them and must preserve the designer intent in the handoff guide.

---

## 9. Testing and CI ownership

1. **Library pipeline is sacred:** `emojify:preTest`/`test`/`postTest` in order, plus `spotlessCheck` and module assembly, run in CI today and remain untouched. Ownership stays with the `@builder` until handoff, then the reviewer.
2. **Current sample exclusion:** `android-ci.yml` ignores `app/src/main/**` paths, sets `CI: true` (which removes `:app` from settings), and runs no sample job. The sample is local-only today. This stays the default.
3. **Preferred JVM tests where possible:** add `internal fun String?.isValidInput(): Boolean` in `app/src/main/java/io/wax911/emojifysample/util/InputGuard.kt` for the null or trimmed-empty decision, so it is unit-testable with JUnit4 in `:app` without a device. Parser behavior itself is already covered by `:emojify` tests.
4. **Explicit local-only/UI CI decision:** Compose UI tests are instrumentation tests and need an emulator. Default is local-only execution; CI execution requires an explicit Oracle-approved emulator job in Phase 3. Nothing runs sample instrumentation in CI silently.
5. **Manual verification matrix:** every Tier 1 PR is manually verified on an emulator at the effective API 23 (minSdk, Android 6.0) and at the current API (37), covering the three conversions, empty-input feedback, keyboard behavior, and landscape. `:app:installDebug` with `CI` unset is the deployment command.
6. **Sample automerge policy:** enabled for the narrow `gradle/sample.versions.toml` Renovate rule and auto-approved by workflow. This is an explicit owner decision despite the sample remaining outside CI; disable it if automated updates demonstrate breakage.

---

## 10. Rollback boundaries and risk register

Each phase lands as its own revertible PR set. Reverting one phase does not require reverting others.

| Boundary | Revert scope | Notes |
|----------|--------------|-------|
| Phase 2 | `gradle/sample.versions.toml`, `settings.gradle.kts`, `buildSrc` files, `app/build.gradle.kts`, `action.yml` cache path | Purely structural; library builds are identical before and after, so revert is trivially safe |
| Phase 3 | `renovate.json`, `auto-approve.yml`, `release-drafter-config.yml`, optional CI job | Close any Renovate PRs opened meanwhile; labels on existing PRs may need manual cleanup |
| Phase 4 spike | decision record doc, Compose aliases, sample plugin in `app/build.gradle.kts` | Removing the plugin and aliases restores the Phase 2 state |
| Tier 1 | `app/src/main/**` (restore XML, ViewBinding, `MainActivity`), sample plugin/deps | Isolated to `:app`; library diff must be empty at all times |
| Phase 5 | Per-item Compose code in `:app` | Each Tier 2 item reverts independently |

Risk register:

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| AGENTS.md drift misleads implementers | High today | D1 decision in Phase 1; docs aligned to code by default |
| Alias duplicated across catalogs, catalogs drift | Medium | Move, never copy; grep acceptance in 6.2 |
| `sampleLibs` accessor unavailable in `buildSrc` compile | Medium | Mirrored `buildSrc` catalog + `implementation(files(...))` workaround; any Gradle task exercises it |
| Renovate/autolabeler misclassifies sample PRs as consumer `dependencies` | Medium | `sample-dependencies` + `skip-changelog` labels, `additionalBranchPrefix`, additive-label documentation, fixture dry-run gate |
| Sample PRs merge without sample CI | Medium | Explicit owner decision, narrow catalog file match, and ongoing review of automated update outcomes |
| Sample PRs automerge into `develop` | Medium | Intentional per-catalog `automerge: true` policy; disable if breakage appears |
| Gradle cache does not invalidate on sample catalog changes | Medium | `cache-dependency-path` glob update + cache key validation (5.4) |
| Compose compiler plugin/Kotlin mismatch | Low | Plugin version pinned to Kotlin 2.4.10 in the sample catalog; spike verifies first |
| Activity or theme change breaks startup or manager access | Low | Spike records the contract first; Option A default; initializer untouched |
| Spotless misses new Compose code | Medium | Extend Spotless to `:app` in Tier 1; local enforcement even without sample CI |
| Tier 2 scope leaks into Tier 1 | Medium | Fixer scope bounded by designer guide; reviewer gate checks for Tier 2 artifacts |
| Sample regressions invisible (no CI) | Medium | Manual matrix on API 23 and current API; JVM tests for guard logic; monitor automated sample dependency updates |
| Library diff contaminated by sample work | Low | Tier 1 PR must show empty diff on `emojify/`, `contract/`, `serializer/` |

---

## 11. Decisions requiring user input now

Five decisions, with recommended defaults. The Oracle (`wax911`) records each in Phase 1 before implementation proceeds.

| ID | Decision | Recommended default |
|----|----------|---------------------|
| D1 | `AGENTS.md` drift (Kotlin 2.4.10, Gradle 9.6.1, SDK 37, minSdk 23, and the `:app`-only logic rule contradiction) | Align docs to the effective build baseline; update `AGENTS.md` in the Phase 1 patch |
| D2 | Activity contract for Compose | Retain `AppCompatActivity` (Option A) unless the Phase 4 spike proves a blocker; spike records the evidence |
| D3 | Empty-input feedback wording | Explicit feedback `Enter text before converting` with no parser invocation, replacing the legacy `You must first enter some text` behavior and the null-only check |
| D4 | Sample automerge policy | Enable sample catalog auto-approval and auto-merge despite the sample remaining outside CI; revisit if automated updates expose breakage |
| D5 | Consumer changelog treatment of sample-only PRs | Exclude sample-only PRs from consumer changelogs via `skip-changelog`; they are not a consumer release concern |

---

*End of roadmap. Companion document: `docs/sample-app-compose-ui-guidelines.md`.*
