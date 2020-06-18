package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.common.isLibraryModule
import io.wax911.emoji.buildSrc.common.isSampleModule
import org.gradle.api.Project

private fun Project.applyModulePlugin() {
    if (isLibraryModule()) {
        plugins.apply("com.android.library")
        plugins.apply("org.jetbrains.dokka")
        plugins.apply("maven-publish")
    }
    else
        plugins.apply("com.android.application")
}

internal fun Project.configurePlugins() {
    applyModulePlugin()
    plugins.apply("kotlin-android")
    if (isSampleModule())
        plugins.apply("kotlin-android-extensions")
}