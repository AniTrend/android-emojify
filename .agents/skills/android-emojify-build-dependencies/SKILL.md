---
name: android-emojify-build-dependencies
description: 'Understand and change android-emojify build logic, module dependencies, version catalog entries, Dokka setup, Spotless, and shared Gradle conventions. Use for buildSrc edits, new dependencies, module graph changes, or documentation pipeline work.'
argument-hint: 'Describe the dependency, Gradle change, or build pipeline task you need to make'
---

# Android Emojify Build And Dependencies

## What This Skill Produces

- A safe path for changing module dependencies or shared build logic.
- A map of where versions, plugins, Dokka, Spotless, and Android defaults are defined.
- Clear guidance on whether a change belongs in a module build file, the version catalog, or `buildSrc`.

## When To Use

- Adding or upgrading dependencies.
- Changing module relationships.
- Editing Dokka, Spotless, JDK, Android SDK levels, publishing, or test conventions.
- Understanding how the `io.wax911.emojify` plugin wires modules together.

## Procedure

1. Read the [build map](./references/build-map.md) to find the owning file for the convention you want to change.
2. If the change introduces or upgrades a dependency, add the version and alias in `gradle/libs.versions.toml` first.
3. If the behavior should apply to many modules, implement it in `buildSrc` instead of duplicating it in several module build files.
4. Check the module graph before adding a project dependency so lower layers (`:contract`) do not depend on higher layers (`:emojify`).
5. Keep Dokka, Spotless, and test behavior aligned with the shared configuration in `buildSrc`.
6. Remember that `:app` is excluded from CI (`settings.gradle.kts` checks for the `CI` environment variable); do not rely on `:app` being present in automated builds.
7. The emojify test pipeline requires `emojify:preTest` → `emojify:test` → `emojify:postTest` in that order; always chain them.
8. When running Gradle locally, use the existing `jenv-gradle-low-ram` skill if Java selection or memory pressure becomes an issue.

## References

- [build map](./references/build-map.md)
