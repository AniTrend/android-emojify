---
name: android-emojify-kdoc-dokka
description: 'Write or improve KDoc for public APIs in android-emojify. Use for Dokka updates, class docs, function docs, property docs, consumer-facing documentation, and explaining how downstream apps should initialize or extend emoji library APIs.'
argument-hint: 'Describe the public API, module, or documentation gap you need to cover'
---

# Android Emojify KDoc And Dokka

## What This Skill Produces

- Consumer-facing KDoc that reads well on the published Dokka site.
- Documentation that explains serializer contracts, parsing behavior, lifecycle expectations, and module context.
- A repeatable checklist for updating docs whenever public behavior changes.

## When To Use

- Adding or changing a public class, interface, annotation, function, property, or enum.
- Explaining how a downstream app should initialize `EmojiManager`, plug in a serializer, or invoke `EmojiParser`.
- Documenting `IEmojiDeserializer` implementations so consumers know which JSON library is required.
- Tightening documentation before a release or after a behavior change.

## Procedure

1. Identify the public or protected surface that changed.
2. Read the [KDoc checklist](./references/kdoc-checklist.md) and match the API shape to the closest template.
3. Document what the API does, when to use it, and what a consumer is expected to provide or observe.
4. For `IEmojiDeserializer` implementations, explain which serialization library backs the implementation and any setup the consumer must do before passing it to `EmojiManager`.
5. For `EmojiParser`, describe the available parsing actions (`FitzpatrickAction`) and their effect on the output string.
6. Link adjacent types with KDoc references so Dokka helps consumers navigate the API surface.
7. If the type belongs to a new package area, consider whether nearby package or module docs also need updating.

## Quality Bar

- Summary first, details second.
- Avoid tautologies such as repeating the type name without explaining behavior.
- Keep docs aligned with real behavior in the code, not the intended behavior from an older implementation.
- `reportUndocumented = true` is active; undocumented public APIs surface as Dokka warnings.

## References

- [KDoc checklist](./references/kdoc-checklist.md)
