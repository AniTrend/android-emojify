package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.plugin.extensions.isLibraryModule
import io.wax911.emoji.buildSrc.plugin.extensions.isSampleModule
import io.wax911.emoji.buildSrc.plugin.extensions.libs
import org.gradle.api.Project

internal fun Project.configurePlugins() {
    if (isLibraryModule()) {
        plugins.apply("com.android.library")
        plugins.apply("com.diffplug.spotless")
        plugins.apply("org.jetbrains.dokka")
        plugins.apply("maven-publish")
    } else {
        plugins.apply("com.android.application")
        plugins.apply("com.diffplug.spotless")
    }
    plugins.apply("kotlin-android")
    if (isSampleModule()) {
        plugins.apply(libs.plugins.compose.compiler.get().pluginId)
    }
}
