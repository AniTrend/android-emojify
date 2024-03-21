package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.plugin.extensions.isLibraryModule
import org.gradle.api.Project

internal fun Project.configurePlugins() {
    if (isLibraryModule()) {
        plugins.apply("com.android.library")
        plugins.apply("com.diffplug.spotless")
        plugins.apply("org.jetbrains.dokka")
        plugins.apply("maven-publish")
    } else
        plugins.apply("com.android.application")
    plugins.apply("kotlin-android")
}
