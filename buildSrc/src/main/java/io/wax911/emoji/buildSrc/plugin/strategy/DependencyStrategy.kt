package io.wax911.emoji.buildSrc.plugin.strategy

import io.wax911.emoji.buildSrc.Libraries
import io.wax911.emoji.buildSrc.common.sample
import org.gradle.api.artifacts.dsl.DependencyHandler

internal class DependencyStrategy(
    private val module: String
) {

    private fun DependencyHandler.applyDefaultDependencies() {
        add("implementation", Libraries.JetBrains.Kotlin.stdlib)
        // Testing libraries
        add("testImplementation", Libraries.junit)
        add("testImplementation", Libraries.mockk)
    }

    private fun DependencyHandler.applyTestDependencies() {
        add("androidTestImplementation", Libraries.AndroidX.Test.core)
        add("androidTestImplementation", Libraries.AndroidX.Test.rules)
        add("androidTestImplementation", Libraries.AndroidX.Test.runner)
        add("testImplementation", Libraries.AndroidX.Test.Extension.junitKtx)
    }

    private fun DependencyHandler.applyLifeCycleDependencies() {
        add("implementation", Libraries.AndroidX.Lifecycle.liveDataCoreKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.runTimeKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.liveDataKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.extensions)
    }

    fun applyDependenciesOn(handler: DependencyHandler) {
        handler.applyDefaultDependencies()
        handler.applyTestDependencies()
        if (module == sample)
            handler.applyLifeCycleDependencies()
    }
}