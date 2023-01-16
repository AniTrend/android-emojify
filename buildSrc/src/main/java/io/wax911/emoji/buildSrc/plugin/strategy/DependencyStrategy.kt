package io.wax911.emoji.buildSrc.plugin.strategy

import io.wax911.emoji.buildSrc.plugin.extensions.*
import io.wax911.emoji.buildSrc.plugin.extensions.androidTestImplementation
import io.wax911.emoji.buildSrc.plugin.extensions.implementation
import io.wax911.emoji.buildSrc.plugin.extensions.testImplementation
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

internal class DependencyStrategy(private val project: Project) {

    private fun DependencyHandler.applyLoggingDependencies() {
        implementation(project.libs.timber)
    }

    private fun DependencyHandler.applyDefaultDependencies() {
        implementation(project.libs.jetbrains.kotlin.stdlib.jdk8)
        implementation(project.libs.jetbrains.kotlin.reflect)
        // Testing libraries
        testImplementation(project.libs.junit)
        testImplementation(project.libs.mockk)
    }

    private fun DependencyHandler.applyTestDependencies() {
        androidTestImplementation(project.libs.androidx.test.core.ktx)
        androidTestImplementation(project.libs.androidx.test.rules)
        androidTestImplementation(project.libs.androidx.test.runner)
        androidTestImplementation(project.libs.mockk.android)
        testImplementation(project.libs.androidx.test.ext.junit.ktx)
    }

    private fun DependencyHandler.applyLifeCycleDependencies() {
        implementation(project.libs.androidx.lifecycle.livedata.core.ktx)
        implementation(project.libs.androidx.lifecycle.runtime.ktx)
        implementation(project.libs.androidx.lifecycle.livedata.ktx)
        implementation(project.libs.androidx.lifecycle.extensions)
    }

    private fun DependencyHandler.applyCoroutinesDependencies() {
        implementation(project.libs.jetbrains.kotlinx.coroutines.android)
        implementation(project.libs.jetbrains.kotlinx.coroutines.core)
        testImplementation(project.libs.jetbrains.kotlinx.coroutines.test)
        androidTestImplementation(project.libs.cash.turbine)
    }

    fun applyDependenciesOn(handler: DependencyHandler) {
        handler.applyLoggingDependencies()
        handler.applyDefaultDependencies()
        handler.applyTestDependencies()
        if (project.isSampleModule()) {
            handler.applyLifeCycleDependencies()
            handler.applyCoroutinesDependencies()
        }
    }
}