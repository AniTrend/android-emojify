---
description: Use when making module graph changes, package ownership changes, public API shifts, build logic updates, documentation workflow changes, or editing repository customizations in android-emojify. Keeps instructions, skills, AGENTS guidance, and consumer-facing context aligned with current repository behavior.
applyTo: build.gradle.kts, settings.gradle.kts, gradle/**/*.toml, gradle/**/*.properties, buildSrc/**/*.kt, */build.gradle.kts, */src/main/**/*.kt, .github/instructions/*.md, .github/skills/**, AGENTS.md, README.md
---

# Context Maintenance Guidance

- When a change materially alters repository reality, update the relevant repo guidance in the same patch instead of leaving instructions and skills stale.
- Treat the following as context-bearing assets that may need maintenance after major changes: `.github/instructions/*.md`, `.github/skills/**`, `AGENTS.md`, and `README.md`.
- Audit repo guidance when you change module boundaries, dependency direction, package ownership, shared build conventions, Dokka behavior, serializer schema, or consumer-facing extension points.
- Audit KDoc and Dokka guidance when public APIs, serializer contracts, emoji data schema, or downstream integration patterns change.
- Audit the `android-emojify-reference-map` and `android-emojify-build-dependencies` skills when a module gains a new responsibility, a new package root becomes important, or the build and publishing workflow changes.
- Remove or rewrite contradictory guidance instead of layering new instructions on top of obsolete ones.
- Prefer updating an existing instruction or skill when the workflow still fits; add a new instruction or skill only when a genuinely new recurring concern appears.
- Keep changes specific and low-churn: update only the files whose guidance is no longer true.
- If a change affects how downstream apps should discover, import, or configure android-emojify APIs, update the relevant consumer-facing guidance in the same change.
- If the Java version pin changes, update `.java-version`, the `context.instructions.md` Build And Tooling Facts section, and the `jenv-gradle-low-ram` skill examples accordingly.
- If the emoji JSON schema changes, verify serializer parity across `kotlinx`, `gson`, and `moshi`, and update the reference map and build-dependencies skills if module responsibilities shift.
- If repository memory is available, record durable repo facts after significant architectural or workflow changes so future tasks start with current context.
