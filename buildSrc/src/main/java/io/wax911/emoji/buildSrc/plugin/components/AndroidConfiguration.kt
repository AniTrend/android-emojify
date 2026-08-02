package io.wax911.emoji.buildSrc.plugin.components

import com.android.build.gradle.internal.dsl.DefaultConfig
import io.wax911.emoji.buildSrc.plugin.extensions.baseAppExtension
import io.wax911.emoji.buildSrc.plugin.extensions.baseExtension
import io.wax911.emoji.buildSrc.plugin.extensions.isLibraryModule
import io.wax911.emoji.buildSrc.plugin.extensions.isSampleModule
import io.wax911.emoji.buildSrc.plugin.extensions.libs
import io.wax911.emoji.buildSrc.plugin.extensions.props
import io.wax911.emoji.buildSrc.plugin.extensions.spotlessExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.File

internal fun Project.configureSpotless() {
    if (isLibraryModule() || isSampleModule())
        spotlessExtension().run {
            kotlin {
                target("**/kotlin/**/*.kt", "**/java/**/*.kt")
                targetExclude(
                    "${layout.buildDirectory.get()}/**/*.kt",
                    "**/test/**/*.kt",
                    "**/androidTest/**/*.kt",
                    "bin/**/*.kt",
                )
                val ktlintConfig = ktlint(libs.versions.ktlint.get())
                if (isSampleModule()) {
                    // ktlint 1.0.1 flags @Composable names otherwise. Sample-only override so
                    // library ktlint configuration stays byte-identical.
                    ktlintConfig.editorConfigOverride(
                        mapOf(
                            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                        ),
                    )
                }
                licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
            }
        }
}

private fun Project.configureLint() = baseAppExtension().run {
    lint {
        abortOnError = false
        ignoreWarnings = false
        ignoreTestSources = true
    }
}

private fun DefaultConfig.applyAdditionalConfiguration(project: Project) {
    if (project.isSampleModule()) {
        applicationId = "io.wax911.emoji.sample"
        project.baseAppExtension().buildFeatures {
            compose = true
        }
        println("Applying vector drawables configuration for module -> ${project.path}")
        vectorDrawables.useSupportLibrary = true
    } else
        consumerProguardFiles.add(File("consumer-rules.pro"))
}

internal fun Project.configureAndroid(): Unit = baseExtension().run {
    compileSdkVersion(37)
    defaultConfig {
        minSdk = 23
        targetSdk = 37
        versionCode = props[PropertyTypes.CODE].toInt()
        versionName = props[PropertyTypes.VERSION]
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applyAdditionalConfiguration(project)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/NOTICE.*")
        resources.excludes.add("META-INF/LICENSE*")
    }

    sourceSets {
        map { androidSourceSet ->
            androidSourceSet.java.srcDir(
                "src/${androidSourceSet.name}/kotlin",
            )
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    if (isSampleModule()) {
        configureLint()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType(KotlinJvmCompile::class.java) {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.withType(KotlinCompilationTask::class.java) {
        val compilerArgumentOptions = mutableListOf(
            "-opt-in=kotlin.ExperimentalStdlibApi",
        )
        if (isSampleModule()) {
            compilerArgumentOptions.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            compilerArgumentOptions.add("-opt-in=kotlinx.coroutines.FlowPreview")
        }

        compilerOptions {
            allWarningsAsErrors.set(false)
            // Filter out modules that won't be using coroutines
            freeCompilerArgs.addAll(compilerArgumentOptions)
        }
    }
}
