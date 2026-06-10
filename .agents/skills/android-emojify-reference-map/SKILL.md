---
name: android-emojify-reference-map
description: 'Reference map for android-emojify modules, package roots, dependency direction, consumer entry points, and Dokka navigation. Use for questions like which module should own this code, where a class should live, what consumers should import, or how the library is organized.'
argument-hint: 'Describe the feature, type, or consumer workflow you are trying to place or understand'
---

# Android Emojify Reference Map

## What This Skill Produces

- A fast module-placement decision for new or existing code.
- A package-level map of where to search next.
- A consumer-oriented view of which abstractions are likely to be imported, implemented, or extended.

## When To Use

- Choosing where a new class, interface, helper, or data type belongs.
- Understanding which module a consumer should depend on.
- Mapping a downstream use case back to the owning android-emojify package.
- Explaining repo structure before deeper implementation work.

## Procedure

1. Start with the [module reference map](./references/module-map.md) and identify the lowest module that can own the behavior.
2. Match the task to a package family before picking a file. For example: serialization contracts go to `:contract`, concrete deserializers go to `:serializer:*`, parsing logic goes to `:emojify`.
3. Confirm the existing dependency direction so you do not pull a low-level module upward.
4. Open the corresponding Dokka page for the module if you need consumer-facing context or neighboring public types.
5. If the task changes a public API, also apply the `android-emojify-kdoc-dokka` skill so the published docs stay aligned.

## Outputs To Aim For

- Module name
- Candidate package or namespace
- Relevant neighboring abstractions
- Consumer impact summary

## References

- [module reference map](./references/module-map.md)
