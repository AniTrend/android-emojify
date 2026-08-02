# Jetpack Compose UI/UX Guidelines for the Emojify Sample App

**Scope:** This document splits the sample app Compose migration into two tiers. Tier 1 is the bounded fixer scope and must strictly preserve existing behavior. Tier 2 contains approved enhancements that are documented here but are not part of the bounded fixer acceptance criteria.

**Audience:** A later bounded `@fixer` implementation pass.

**Status:** Planning only, Phase 1.

---

## 1. Tier 1: strict parity migration (bounded fixer scope)

Tier 1 migrates the existing single-screen XML layout to Jetpack Compose while preserving every behavior that can be proven from the current source. No new features, no new visual embellishments, and no new architecture decisions are allowed in Tier 1.

### 1.1 Source-derived behaviors that must be preserved

The current implementation lives in `app/src/main/java/io/wax911/emojifysample/MainActivity.kt`, `app/src/main/res/layout/content_main.xml`, and `app/src/main/res/values/strings.xml`. The following behaviors are proven from source.

1. **Single screen.** The manifest declares a single `MainActivity` with the `LAUNCHER` intent filter. There is no navigation graph.
2. **App name.** The app label is `Emojify` from `strings.xml`.
3. **Input area.** A multiline text field with a hint inviting text entry.
4. **Three conversion actions.** The layout exposes three clickable labels: `Emoji`, `Html`, and `Hex`. The code maps these to `EmojiManager.parseToUnicode`, `EmojiManager.parseToHtmlDecimal`, and `EmojiManager.parseToHtmlHexadecimal`.
5. **In-place replacement.** Each conversion replaces the content of the input field with the converted result.
6. **Empty input guard.** The current code checks for a null `Editable` and shows a toast. The intended Tier 1 behavior is stricter: null or zero-length trimmed input must show feedback and must not call any parser. This is intended behavior that requires regression coverage. It is not a claim that the current nullable `Editable` implementation already handles blank or whitespace-only input.
7. **Manager access.** The activity obtains `EmojiManager` from the `Application` subclass `App` via the `FragmentActivity.emojiManager()` extension in `ContextExt.kt`.
8. **Initializer behavior.** The app uses `EmojiInitializer` and `AppInitializer` in the manifest. This behavior must remain unchanged.

### 1.2 Prerequisite: activity compatibility spike

Do not pre-decide to replace `AppCompatActivity` with `ComponentActivity`. A compatibility spike must run before the bounded fixer begins and must document one of the following options.

- **Option A: retain `AppCompatActivity`.** Add Compose content through `androidx.activity.compose.setContent` while keeping the existing activity class and `FragmentActivity` extension. This is the lower-risk option.
- **Option B: migrate to `ComponentActivity`.** Replace the activity class and update any `FragmentActivity` dependency in the manager accessor. This may require changes to `ContextExt.kt` or the `ViewModel` owner.

The bounded fixer must not change the activity class or the manager accessor contract until the spike documents the decision and any required migration steps.

### 1.3 Tier 1 implementation order

1. Add Compose library dependencies to `app/build.gradle.kts` using `sampleLibs` from `gradle/sample.versions.toml`; keep the Kotlin Compose compiler plugin and Kotlin version in the shared catalog.
2. Create a minimal Compose theme using the Material 3 baseline light color scheme. No dark theme, no dynamic color, no custom font.
3. Create a `MainScreen` composable inside the existing activity, after the spike decides the activity class.
4. Implement the multiline `OutlinedTextField` with the Tier 1 hint and label.
5. Implement a horizontal row of three conversion buttons labeled `Emoji`, `HTML`, and `Hex`.
6. Implement the in-place replacement behavior: after a successful conversion, update the input field text with the converted result.
7. Implement the empty input guard: if the input is null or zero-length after trimming, show a toast with the message `Enter text before converting` and do not call any parser method.
8. Add basic accessibility: visible labels, 48dp by 48dp touch targets, and logical focus order.
9. Remove the XML layouts, `ViewBinding`, and AppCompat theme assets that are replaced by Compose.
10. Run the regression checklist in section 1.9.

### 1.4 Tier 1 component structure

```
MainActivity (activity class TBD by compatibility spike)
└── setContent { EmojifyTheme { MainScreen() } }
    ├── SmallTopAppBar
    ├── InputSection
    │   └── OutlinedTextField (multiline)
    └── ConversionActions
        ├── Emoji button
        ├── HTML button
        └── Hex button
```

No separate output card, no quick emoji bar, no copy/clear actions, no loading overlay, and no error banner are allowed in Tier 1.

### 1.5 Tier 1 state and behavior

Tier 1 uses synchronous conversion. The only behavioral states are:

- **Ready.** The input field is enabled and the three conversion buttons are enabled.
- **Converting.** Optional transient state while the conversion runs. Because the existing parser calls are synchronous, this state may be imperceptible. Do not introduce async parsing, loading spinners, or error surfaces in Tier 1.

The empty input guard must run before any parser call. If the guard triggers, the state remains Ready.

### 1.6 Tier 1 design tokens

Use the Material 3 baseline tokens. Do not add custom fonts, dark themes, or dynamic color.

| Token | Tier 1 value |
|-------|---------------|
| Color scheme | Material 3 baseline light scheme. |
| Typography | System font family (Roboto on most devices). Use Material 3 defaults. |
| Shape | Material 3 default shape scale. |
| Spacing | 4dp grid. Screen padding 16dp. Between sections 16dp to 24dp. |
| Elevation | Flat surfaces only. No elevated cards in Tier 1. |

### 1.7 Tier 1 layout

- Single column layout only.
- No adaptive breakpoints and no two-column layout.
- The layout must be scrollable and must remain usable when the system keyboard is open. Use `imePadding()` on the scrollable container.
- The conversion action row must stay at a fixed vertical position so repeated taps do not require chasing moving targets.
- Screen padding is 16dp on all sides.

### 1.8 Tier 1 accessibility

- Every conversion button must have a minimum 48dp by 48dp touch target.
- The input field must have a visible label and a hint. The label can be `Input` and the hint can be `Enter text with emoji, shortcodes, or HTML entities`.
- Each conversion button must have a semantic label for TalkBack: `Convert to emoji`, `Convert to HTML`, and `Convert to hexadecimal HTML`.
- No emoji picker exists in Tier 1, so there are no emoji contentDescription decisions to make.
- Focus order must be logical: input field, then the three conversion buttons in visual order.

### 1.9 Tier 1 content wording

| Element | Tier 1 text |
|---------|--------------|
| App title | `Emojify` |
| Input label | `Input` |
| Input hint | `Enter text with emoji, shortcodes, or HTML entities` |
| Emoji button | `Emoji` |
| HTML button | `HTML` |
| Hex button | `Hex` |
| Empty input feedback | `Enter text before converting` |

### 1.10 Tier 1 acceptance criteria

- [ ] The activity still hosts a single screen with no navigation graph.
- [ ] The app label remains `Emojify`.
- [ ] The screen uses Compose and contains no legacy XML layouts or ViewBinding references.
- [ ] The input field is multiline and shows the Tier 1 hint.
- [ ] Three conversion buttons labeled `Emoji`, `HTML`, and `Hex` are visible and reachable while the keyboard is open.
- [ ] The `Emoji` button calls `EmojiManager.parseToUnicode` and replaces the input text with the result.
- [ ] The `HTML` button calls `EmojiManager.parseToHtmlDecimal` and replaces the input text with the result.
- [ ] The `Hex` button calls `EmojiManager.parseToHtmlHexadecimal` and replaces the input text with the result.
- [ ] Null or zero-length trimmed input shows the empty input feedback and does not call any parser.
- [ ] The `EmojiManager` is obtained from the existing `Application`/`ContextExt` contract. No new manager accessor is introduced.
- [ ] The `EmojiInitializer` and manifest provider remain unchanged.
- [ ] All interactive controls have a minimum 48dp by 48dp touch target.
- [ ] The input field and all three buttons have accessible labels for TalkBack.
- [ ] The layout is a single scrollable column and works in portrait and landscape without horizontal scrolling.
- [ ] The implementation uses only the Material 3 baseline light theme.
- [ ] No new features from Tier 2 are implemented.

---

## 2. Tier 2: separately approved enhancements

The following sections describe improvements that are approved for the product direction but are explicitly not part of the bounded fixer scope. A later phase, separate PR, or separate approval must implement each item. The bounded fixer must not implement any of these.

### 2.1 Loading and async parsing

- Run conversions on a background dispatcher such as `Dispatchers.Default` because the parser can be slow for long text.
- Show a transient inline loading indicator on the active conversion button while a conversion runs.
- Show an initial loading overlay only if the `EmojiManager` is not yet initialized by the startup provider.

### 2.2 Error recovery

- Catch parser exceptions and initialization failures and map them to an `errorMessage` field in a `UiState`.
- Show an inline error banner or `Snackbar` with a `Retry` action.
- Keep the input field content intact when an error occurs.

### 2.3 Dark theme and dynamic color

- Add a dark color scheme. The color values in the previous draft document are a candidate starting point but are not required for Tier 1.
- Optionally support dynamic color on Android 12 and above. The compatibility spike should decide whether dynamic color is in scope for Tier 2.

### 2.4 Adaptive layouts beyond single column

- Add responsive breakpoints for phone landscape, tablets, and foldable devices.
- At 600dp width and above, use a two-column layout with input on the left and actions/result on the right.
- At 840dp width and above, center the content in a max-width container of 720dp.

### 2.5 Custom font

- Add a display font such as Nunito for the app title. The previous draft suggested bundling `res/font/nunito_bold.ttf` and applying it only to `displaySmall`.
- Keep body and label text in the system font for accessibility.

### 2.6 Motion and reduced motion

- Add subtle entrance, result, and error transitions using `AnimatedVisibility` and `AnimatedContent`.
- Keep durations between 150ms and 300ms.
- Respect `prefers-reduced-motion` and disable animations when requested.

### 2.7 Quick emoji bar

- Add a horizontal scrollable row of common emoji chips.
- Tapping a chip inserts the emoji at the current cursor position.
- Each chip must have a minimum 48dp by 48dp touch target and a human-readable label for TalkBack, such as `grinning face` or `thumbs up`. Do not use raw Unicode values as labels.

### 2.8 Copy and clear actions

- Add a `Copy` action that copies the output to the clipboard and shows a `Copied` confirmation.
- Add a `Clear` action that resets the input and any result area.

### 2.9 Separate output behavior

- Keep the original input visible in the input field and show the converted result in a separate read-only output card.
- This changes the current in-place behavior, so it must be approved and tested as a distinct feature.

### 2.10 Advanced accessibility polish

- Mark the result card as a live region so TalkBack announces new output.
- Add a live region for error banners.
- Verify full keyboard navigation order and focus grouping.
- Test text scaling up to 200%.

### 2.11 Tier 2 acceptance criteria (not part of bounded fixer)

These items are listed only for future reference. The bounded fixer does not need to satisfy them.

- [ ] Async parsing with loading indicators.
- [ ] Error surfaces with retry actions.
- [ ] Dark theme support.
- [ ] Optional dynamic color support.
- [ ] Adaptive two-column layout for wide screens.
- [ ] Custom display font for the title.
- [ ] Subtle motion that respects reduced motion.
- [ ] Quick emoji bar with human-readable labels.
- [ ] Copy and clear actions.
- [ ] Separate input and output areas.
- [ ] Advanced TalkBack and keyboard support.

---

## 3. Behaviors that are not proven from source

The README screenshots show UI elements that are not reflected in the current source. Do not implement these as part of Tier 1.

- **Fourth "Short" button.** Screenshots show `Emoji`, `Html`, `Hex`, and `Short`. The current source only defines `Emoji`, `Html`, and `Hex`. The `Short` conversion uses `parseToAliases`, which is documented but not wired in the current activity.
- **Top emoji bar.** Some screenshots show a horizontal row of rendered emoji above the input field. This component does not exist in the current XML or Kotlin source.
- **Keyboard auto-focus.** The current layout sets `inputType="textMultiLine"`. Whether the keyboard should open automatically or a specific IME action should be used is not specified.
- **Output destination.** The source replaces the input field text in place. Any separate output card or copy action is a Tier 2 enhancement.
- **Error surfaces.** The current code has no exception handling for parser failures or initialization failures.

---

## 4. Implementation notes for the fixer

### 4.1 Dependencies

Add Compose library dependencies in `app/build.gradle.kts` using the versions defined in `gradle/sample.versions.toml` through `sampleLibs`, after the catalog split phase. Keep the Kotlin Compose compiler plugin and its Kotlin version reference in the shared catalog. Do not add libraries that are only needed for Tier 2.

### 4.2 Activity decision

Wait for the compatibility spike decision in section 1.2 before changing the activity class. If the spike chooses Option A, keep `AppCompatActivity` and use `setContent` from the activity compose artifact. If the spike chooses Option B, migrate to `ComponentActivity` and update any affected accessors.

### 4.3 State management

Tier 1 does not require a `ViewModel`. Keep state in the activity using `rememberSaveable` if needed. A `ViewModel` is only required if Tier 2 async parsing is implemented.

### 4.4 Testing

- Add a Compose UI test that verifies each of the three conversion actions produces the correct text in the input field.
- Add a regression test that verifies null and blank input show the empty input feedback and do not invoke the parser.
- Test on the effective UI baseline of API 23 (Android 6.0). The project build configuration must not be changed; the baseline is stated for testing focus only.

### 4.5 Cleanup

Remove the following files and references once the Compose screen is verified.

- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/content_main.xml`
- `app/src/main/res/values/styles.xml` and `app/src/main/res/values-v23/styles.xml` if they are fully replaced by Compose theming
- `ViewBinding` import and usage in `MainActivity`
- Any unused drawable resources that were only used by the legacy XML layouts

Keep `app/src/main/res/values/colors.xml` and `app/src/main/res/values/strings.xml` if they still contain values used by the manifest or launcher icon.

---

## 5. Related files

- `app/src/main/java/io/wax911/emojifysample/MainActivity.kt`
- `app/src/main/java/io/wax911/emojifysample/App.kt`
- `app/src/main/java/io/wax911/emojifysample/EmojiInitializer.kt`
- `app/src/main/java/io/wax911/emojifysample/ext/ContextExt.kt`
- `app/src/main/res/layout/content_main.xml`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values-v23/styles.xml`
- `app/src/main/AndroidManifest.xml`

---

*End of guidelines.*
