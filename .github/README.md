# GitHub Copilot Chat Customization

This directory contains GitHub Copilot Chat customization files for the android-emojify repository.

## Structure

- `instructions/` - Repository-specific context and guidance applied automatically to Copilot Chat sessions
- `skills/` - Reusable procedural skills for common development tasks

## Usage

These files are automatically recognized by GitHub Copilot Chat to provide enhanced assistance tailored to the android-emojify library.

### Instructions

| File | Purpose |
| --- | --- |
| `context.instructions.md` | Overall architecture, module boundaries, build facts, and working heuristics |
| `build-logic.instructions.md` | Guidance for editing Gradle files, buildSrc, version catalog, and CI workflows |
| `kdoc-dokka.instructions.md` | KDoc style and Dokka requirements for public APIs |
| `context-maintenance.instructions.md` | How to keep instructions, skills, and AGENTS.md aligned after changes |

### Skills

| Skill | Purpose |
| --- | --- |
| `jenv-gradle-low-ram` | Align `jenv` to `.java-version` (`21.0.8`) and run Gradle safely on memory-constrained machines |
| `android-emojify-build-dependencies` | Navigate and change build logic, module deps, and version catalog entries |
| `android-emojify-kdoc-dokka` | Write or improve KDoc for consumer-facing APIs |
| `android-emojify-reference-map` | Determine which module and package should own new or moved code |
