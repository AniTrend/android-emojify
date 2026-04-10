---
description: Use when adding or changing public Kotlin APIs, KDoc, Dokka output, class docs, function docs, or property docs in android-emojify modules.
applyTo: emojify/src/main/**/*.kt, contract/src/main/**/*.kt, serializer/*/src/main/**/*.kt
---

# KDoc And Dokka Guidance

- Treat KDoc as consumer documentation. The generated Dokka site is how downstream apps learn the library surface: `https://anitrend.github.io/android-emojify/`.
- Document every new or changed public or protected class, interface, object, enum, annotation, function, and property that a consumer may touch.
- Write documentation for someone outside this repo who does not already know the emoji parsing architecture. Explain what the API is for, when to use it, and which module or workflow it belongs to.
- For `IEmojiDeserializer` implementations, document which JSON format or serialization library is used, any required configuration, and how to wire the deserializer into `EmojiManager`.
- For `EmojiManager` and `EmojiParser`, document the expected initialization order, threading assumptions, and Fitzpatrick modifier behavior where relevant.
- For trie helpers and parser internals, document the algorithmic contract and any performance characteristics that affect callers.
- For extension functions and properties, document the receiver, side effects, threading or lifecycle assumptions, and any important nullability or mutation behavior.
- For classes with important collaborators, link to nearby types with KDoc references instead of forcing consumers to search the repo manually.
- Preserve the existing house style: a short summary first, then focused detail, with `@param`, `@property`, `@return`, `@throws`, `@see`, and `@since` where they add value.
- Do not invent version history. Only add `@since` when the version is already known or established in adjacent code.
- Avoid placeholder KDoc that only restates the type name. Explain behavior, expectations, and integration points.
- If behavior changes, update the docs in the same patch so the published site stays trustworthy.
- Packages under `.*\.internal.*` are suppressed from Dokka. If an API is meant for library consumers, keep it in a documented public package.
- `reportUndocumented = true` is configured globally in `buildSrc/…/components/AndroidOptions.kt`; undocumented public APIs surface as Dokka warnings and are not optional.
