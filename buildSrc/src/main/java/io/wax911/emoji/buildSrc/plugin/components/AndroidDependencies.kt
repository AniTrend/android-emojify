package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.plugin.extensions.implementation
import io.wax911.emoji.buildSrc.plugin.strategy.DependencyStrategy
import org.gradle.api.Project

internal fun Project.configureDependencies() {
    val dependencyStrategy = DependencyStrategy(project)
    dependencies.implementation(
        fileTree("libs") {
            include("*.jar")
        },
    )
    dependencyStrategy.applyDependenciesOn(dependencies)
}
