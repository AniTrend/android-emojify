package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.common.Configuration
import com.android.build.gradle.internal.dsl.DefaultConfig
import io.wax911.emoji.buildSrc.plugin.extensions.*
import io.wax911.emoji.buildSrc.plugin.extensions.baseAppExtension
import io.wax911.emoji.buildSrc.plugin.extensions.baseExtension
import io.wax911.emoji.buildSrc.plugin.extensions.spotlessExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.File

internal fun Project.configureSpotless() {
    if (isLibraryModule())
        spotlessExtension().run {
            kotlin {
                target("**/kotlin/**/*.kt")
                targetExclude("$buildDir/**/*.kt", "bin/**/*.kt")
                ktlint(libs.versions.ktlint.get()).userData(
                    mapOf("android" to "true")
                )
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

@Suppress("UnstableApiUsage")
private fun DefaultConfig.applyAdditionalConfiguration(project: Project) {
    if (project.isSampleModule()) {
        applicationId = "io.wax911.emoji.sample"
        project.baseAppExtension().buildFeatures {
            viewBinding = true
        }
        println("Applying vector drawables configuration for module -> ${project.path}")
        vectorDrawables.useSupportLibrary = true
    }
    else
        consumerProguardFiles.add(File("consumer-rules.pro"))
}

internal fun Project.configureAndroid(): Unit = baseExtension().run {
    compileSdkVersion(Configuration.compileSdk)
    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName
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
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
    }

    sourceSets {
        map { androidSourceSet ->
            androidSourceSet.java.srcDir(
                "src/${androidSourceSet.name}/kotlin"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
            allWarningsAsErrors = false
            kotlinOptions {
                allWarningsAsErrors = false
                val compileArgs = mutableListOf("-opt-in=kotlin.Experimental")
                if (isSampleModule()) {
                    compileArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
                    compileArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
                    compileArgs.add("-opt-in=kotlin.Experimental")
                } else
                    compileArgs.add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
                // Filter out modules that won't be using coroutines
                freeCompilerArgs = compileArgs
            }
        }
    }

    tasks.withType(KotlinJvmCompile::class.java) {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}