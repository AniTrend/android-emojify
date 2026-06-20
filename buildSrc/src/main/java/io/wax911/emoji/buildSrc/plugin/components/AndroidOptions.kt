package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.plugin.extensions.baseExtension
import io.wax911.emoji.buildSrc.plugin.extensions.isLibraryModule
import io.wax911.emoji.buildSrc.plugin.extensions.props
import io.wax911.emoji.buildSrc.plugin.extensions.publishingExtension
import io.wax911.emoji.buildSrc.module.Modules
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

private fun Project.dependenciesOfProject(): List<Modules.Module> {
    return when (project.name) {
        Modules.Library.Emojify.id -> listOf(
            Modules.Library.Contract,
        )
        Modules.Library.SerializerGson.id,
        Modules.Library.SerializerMoshi.id,
        Modules.Library.SerializerKotlinX.id -> listOf(
            Modules.Library.Contract,
        )
        else -> emptyList()
    }
}

private fun Project.createMavenPublicationUsing(sourcesJar: Jar) {
    println("Applying publication configuration on ${project.path}")
    publishingExtension().publications {
        val component = components.findByName("android")

        println("Configuring maven publication options for ${project.path}:maven with component-> ${component?.name}")
        create("maven", MavenPublication::class.java) {
            groupId = "io.wax911.emoji"
            artifactId = project.name
            version = props[PropertyTypes.VERSION]

            artifact(sourcesJar)
            artifact("${project.layout.buildDirectory.get()}/outputs/aar/${project.name}-release.aar")
            from(component)

            pom {
                name.set("android-emojify")
                description.set("This project is an android port of https://github.com/vdurmont/emoji-java which is a lightweight java library that helps you use Emojis in your java applications re-written in Kotlin.")
                url.set("https://github.com/anitrend/android-emojify")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("wax911")
                        name.set("Maxwell Mapako")
                        organizationUrl.set("https://github.com/anitrend")
                    }
                }
            }
        }
    }
}

private fun Project.createDokkaConfiguration() {
    extensions.configure(DokkaExtension::class.java) {
        moduleName.set(project.name)
        dokkaSourceSets.configureEach {
            suppress.set(name.startsWith("test") || name.startsWith("androidTest"))
            skipDeprecated.set(false)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            documentedVisibilities.set(setOf(VisibilityModifier.Public))
            sourceRoots.from(file("src"))

            dependenciesOfProject().forEach { module ->
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl("https://github.com/anitrend/android-emojify/tree/develop/${module.id.replace(":", "/")}/src/main/kotlin")
                    remoteLineSuffix.set("#L")
                }
            }

            externalDocumentationLinks.register("android-ref") {
                url("https://developer.android.com/reference/kotlin/")
                packageListUrl("https://developer.android.com/reference/androidx/package-list")
            }

            perPackageOption {
                matchingRegex.set("kotlin($|\\.).*")
                skipDeprecated.set(false)
                reportUndocumented.set(true)
            }
            perPackageOption {
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }
        }
    }
}

internal fun Project.configureOptions() {
    if (isLibraryModule()) {
        println("Applying additional tasks options for dokka and javadoc on ${project.path}")

        createDokkaConfiguration()

        val sourcesJar by tasks.register("sourcesJar", Jar::class.java) {
            archiveClassifier.set("sources")
            from(baseExtension().sourceSets["main"].java.srcDirs)
        }

        val classesJar by tasks.register("classesJar", Jar::class.java) {
            from("${project.layout.buildDirectory.get()}/intermediates/classes/release")
        }

        artifacts {
            add("archives", classesJar)
            add("archives", sourcesJar)
        }

        createMavenPublicationUsing(sourcesJar)
    }
}
