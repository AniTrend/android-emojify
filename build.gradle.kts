// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("org.jetbrains.dokka")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.jetbrains.kotlin.gradle)
        classpath(libs.jetbrains.kotlin.serialization)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootProject.file("dokka-docs"))
        failOnWarning.set(false)
    }
}

// Declare subprojects for multi-module documentation aggregation
dependencies {
    dokka(project(":contract"))
    dokka(project(":emojify"))
    dokka(project(":serializer:kotlinx"))
    dokka(project(":serializer:gson"))
    dokka(project(":serializer:moshi"))
}
