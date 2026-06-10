# KDoc Checklist

Use these prompts when documenting public APIs in android-emojify.

## Class Or Interface

- What problem does this type solve?
- Which module or workflow is it part of?
- Should consumers instantiate it, subclass it, implement it, or only use it as a parameter?
- What collaborators or neighboring types matter?
- What lifecycle, threading, or initialization order assumptions matter?

Template:

```kotlin
/**
 * Short summary of the type and the workflow it belongs to.
 *
 * Explain when consumers should use, implement, or extend it.
 * Mention important collaborators with KDoc links such as [EmojiManager] or [IEmojiDeserializer].
 *
 * @property ...
 * @since ...
 */
```

## Function

- What does it do for the caller?
- When should it be called?
- What are the side effects, threading assumptions, or lifecycle requirements?
- What does it return or publish?
- What can fail and how does failure surface?

Template:

```kotlin
/**
 * Short summary of the behavior.
 *
 * Add timing, state, or initialization detail when it matters.
 *
 * @param ...
 * @return ...
 * @throws ...
 */
```

## Property

- Is this configuration, state, or a contract consumers must provide?
- When is it read or updated?
- Is it safe to mutate directly, or should callers use another API?

Template:

```kotlin
/**
 * Explains what this property represents and when consumers should read or set it.
 */
```

## Extension Function Or Property

- Document the receiver explicitly.
- Explain hidden dependencies such as context, thread, or coroutine scope.
- Call out side effects and mutations.

## Serializer Implementation (`IEmojiDeserializer`)

- Name the JSON serialization library being used (kotlinx.serialization, Gson, Moshi).
- Explain any required configuration (e.g., registering adapters, configuring a parser instance).
- Describe how to wire the deserializer into `EmojiManager`.

## Repo-Specific Reminders

- Dokka reports undocumented public APIs (`reportUndocumented = true`), so documentation is not optional for consumer-facing surfaces.
- `.internal` packages are suppressed from published docs at `https://anitrend.github.io/android-emojify/`.
- Use `@since` only when the version is known from `gradle/version.properties` or release context.
- If the behavior changed, update the KDoc in the same patch.
- Verify serializer parity (`kotlinx`, `gson`, `moshi`) whenever the emoji schema or `IEmojiDeserializer` contract changes, and update docs for all affected implementations.
