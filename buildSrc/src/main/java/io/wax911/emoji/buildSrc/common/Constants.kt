package io.wax911.emoji.buildSrc.common

import org.gradle.api.Project

internal const val sample = "app"
internal const val emojify = "emojify"

fun Project.isSampleModule() = name == sample
fun Project.isLibraryModule() = name == emojify