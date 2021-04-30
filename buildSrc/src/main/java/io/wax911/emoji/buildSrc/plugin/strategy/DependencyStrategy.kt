package io.wax911.emoji.buildSrc.plugin.strategy

import io.wax911.emoji.buildSrc.Libraries
import io.wax911.emoji.buildSrc.plugin.extensions.isSampleModule
import io.wax911.emoji.buildSrc.plugin.extensions.implementation
import io.wax911.emoji.buildSrc.plugin.extensions.testImplementation
import io.wax911.emoji.buildSrc.plugin.extensions.androidTestImplementation
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

internal class DependencyStrategy(private val project: Project) {

    private fun DependencyHandler.applyLoggingDependencies() {
        implementation(Libraries.timber)
    }

    private fun DependencyHandler.applyDefaultDependencies() {
        implementation(Libraries.JetBrains.Kotlin.stdlib)
        // Testing libraries
        testImplementation(Libraries.junit)
        testImplementation(Libraries.mockk)
    }

    private fun DependencyHandler.applyTestDependencies() {
        androidTestImplementation(Libraries.AndroidX.Test.core)
        androidTestImplementation(Libraries.AndroidX.Test.rules)
        androidTestImplementation(Libraries.AndroidX.Test.runner)
        testImplementation(Libraries.AndroidX.Test.Extension.junitKtx)
    }

    private fun DependencyHandler.applyLifeCycleDependencies() {
        implementation(Libraries.AndroidX.Lifecycle.liveDataCoreKtx)
        implementation(Libraries.AndroidX.Lifecycle.runTimeKtx)
        implementation(Libraries.AndroidX.Lifecycle.liveDataKtx)
        implementation(Libraries.AndroidX.Lifecycle.extensions)
    }

    private fun DependencyHandler.applyCoroutinesDependencies() {
        implementation(Libraries.JetBrains.KotlinX.Coroutines.android)
        implementation(Libraries.JetBrains.KotlinX.Coroutines.core)
        testImplementation(Libraries.JetBrains.KotlinX.Coroutines.test)
        androidTestImplementation(Libraries.CashApp.Turbine.turbine)
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