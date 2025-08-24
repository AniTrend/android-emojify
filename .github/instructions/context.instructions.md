---
applyTo: **
description: This file describes the overall architecture and module structure of the android-emojify project.
---

# Android Emojify Project Overview

Android Emojify is a lightweight Kotlin library that helps you use Emojis in your Android applications. It's a port of [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java) rewritten in Kotlin with Android-specific optimizations.

## Project Structure

The project follows a multi-module architecture organized into distinct layers:

### Core Modules
- **emojify** - The main library module containing the core emoji functionality
- **serializer** - Module for emoji data serialization and parsing
- **contract** - Shared contracts and interfaces
- **app** - Sample application demonstrating library usage

### Build System
- **buildSrc** - Contains custom Gradle plugins and build logic
  - Custom dependency management extensions
  - Android configuration components
  - Spotless code formatting setup
  - Dokka documentation generation

### Key Technologies
- **Kotlin** - Primary language with coroutines support
- **Android Gradle Plugin** - For Android-specific build configuration
- **Room** - For local emoji data storage (if applicable)
- **Timber** - Logging framework
- **JUnit + MockK** - Testing framework

### Module Dependencies
- The `emojify` module is the main library that other applications depend on
- The `app` module serves as both a sample and testing ground for the library
- The `serializer` module handles emoji data processing
- Build modules in `buildSrc` provide shared build logic

### Architecture Principles
- **Separation of Concerns** - Each module has a specific responsibility
- **Dependency Inversion** - Higher-level modules don't depend on lower-level modules directly
- **Testability** - Code is structured to be easily testable with unit and integration tests
- **Performance** - Optimized for Android runtime with efficient emoji lookups

### Usage Pattern
The library is designed to be used as a dependency in Android projects, providing easy emoji support through simple APIs. It's already being used in production in the AniTrend application.

### Documentation
- Main documentation is available at: https://anitrend.github.io/android-emojify/
- Supported emojis are listed in SUPPORTED.md
- API documentation is generated using Dokka