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
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

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
                url.set("https://github.com/anitrend/android-emoji")
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

private fun Project.createDokkaTaskProvider() = tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(project.layout.buildDirectory.dir("docs/dokka").get())

    // Set module name displayed in the final output
    moduleName.set(this@createDokkaTaskProvider.name)

    // Use default or set to custom path to cache directory
    // to enable package-list caching
    // When this is set to default, caches are stored in $USER_HOME/.cache/dokka
    //cacheRoot.set(file("default"))

    dokkaSourceSets {
        configureEach { // Or source set name, for single-platform the default source sets are `main` and `test`
            // Used when configuring source sets manually for declaring which source sets this one depends on
            //dependsOn(dependenciesOfProject().map(Modules.Module::path))

            // Used to remove a source set from documentation, test source sets are suppressed by default
            suppress.set(false)

            // Used to prevent resolving package-lists online. When this option is set to true, only local files are resolved
            offlineMode.set(false) // this is failing in the ci env

            // Use to include or exclude non public members
            includeNonPublic.set(false)

            // Do not output deprecated members. Applies globally, can be overridden by packageOptions
            skipDeprecated.set(false)

            // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
            reportUndocumented.set(true)

            // Do not create index pages for empty packages
            skipEmptyPackages.set(true)

            // This name will be shown in the final output
            //displayName.set("JVM")

            // Platform used for code analysis. See the "Platforms" section of this readme
            platform.set(org.jetbrains.dokka.Platform.jvm)

            // Property used for manual addition of files to the classpath
            // This property does not override the classpath collected automatically but appends to it
            // classpath.from(file("libs/dependency.jar"))

            // List of files with module and package documentation
            // https://kotlinlang.org/docs/reference/kotlin-doc.html#module-and-package-documentation
            //includes.from("packages.md", "extra.md")

            // List of files or directories containing sample code (referenced with @sample tags)
            //samples.from("samples/basic.kt", "samples/advanced.kt")

            // By default, sourceRoots are taken from Kotlin Plugin and kotlinTasks, following roots will be appended to them
            // Repeat for multiple sourceRoots
            sourceRoot(file("src"))

            dependenciesOfProject().forEach { module ->
                // Specifies the location of the project source code on the Web.
                // If provided, Dokka generates "source" links for each declaration.
                // Repeat for multiple mappings
                sourceLink {
                    // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    localDirectory.set(file("src/main/kotlin"))

                    val repository = "https://github.com/anitrend/android-emojify/tree/develop"
                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(URL("$repository/${module.id.replace(":", "/")}/src/main/kotlin"))
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix.set("#L")
                }
            }

            // Used for linking to JDK documentation
            jdkVersion.set(17)

            platform.set(Platform.jvm)

            // Disable linking to online kotlin-stdlib documentation
            noStdlibLink.set(false)

            // Disable linking to online JDK documentation
            noJdkLink.set(false)

            // Disable linking to online Android documentation (only applicable for Android projects)
            noAndroidSdkLink.set(false)

            // Allows linking to documentation of the project"s dependencies (generated with Javadoc or Dokka)
            // Repeat for multiple links
            externalDocumentationLink {
                // Root URL of the generated documentation to link with. The trailing slash is required!
                url.set(URL("https://developer.android.com/reference/kotlin/"))

                // If package-list file is located in non-standard location
                packageListUrl.set(URL("https://developer.android.com/reference/androidx/package-list"))
            }

            // Allows to customize documentation generation options on a per-package basis
            // Repeat for multiple packageOptions
            // If multiple packages match the same matchingRegex, the longuest matchingRegex will be used
            perPackageOption {
                matchingRegex.set("kotlin($|\\.).*") // will match kotlin and all sub-packages of it
                // All options are optional, default values are below:
                skipDeprecated.set(false)
                reportUndocumented.set(true) // Emit warnings about not documented members
                includeNonPublic.set(false)
            }
            // Suppress a package
            perPackageOption {
                matchingRegex.set(".*\\.internal.*") // will match all .internal packages and sub-packages
                suppress.set(true)
            }
        }
    }
}

internal fun Project.configureOptions() {
    if (isLibraryModule()) {
        println("Applying additional tasks options for dokka and javadoc on ${project.path}")

        createDokkaTaskProvider()

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
