package io.wax911.emoji.buildSrc.plugin

import io.wax911.emoji.buildSrc.plugin.components.configureAndroid
import io.wax911.emoji.buildSrc.plugin.components.configureDependencies
import io.wax911.emoji.buildSrc.plugin.components.configureOptions
import io.wax911.emoji.buildSrc.plugin.components.configurePlugins
import io.wax911.emoji.buildSrc.plugin.components.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

open class CorePlugin : Plugin<Project> {

    /**
     * Inspecting available extensions
     */
    private fun Project.availableExtensions() {
        val extensionSchema = project.extensions.extensionsSchema
        extensionSchema.forEach {
            println("Available extension for module ${project.path}: ${it.name} -> ${it.publicType}")
        }
    }

    /**
     * Inspecting available components
     */
    private fun Project.availableComponents() {
        val collectionSchema = project.components.asMap
        collectionSchema.forEach {
            println("Available component for module ${project.path}: ${it.key} -> ${it.value}")
        }
    }

    override fun apply(project: Project) {
        project.configurePlugins()
        project.configureAndroid()
        project.configureOptions()
        project.configureDependencies()
        project.configureSpotless()
        project.availableExtensions()
        project.availableComponents()
    }
}
