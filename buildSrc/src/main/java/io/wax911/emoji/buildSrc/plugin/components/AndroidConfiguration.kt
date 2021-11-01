package io.wax911.emoji.buildSrc.plugin.components

import io.wax911.emoji.buildSrc.common.Versions
import com.android.build.gradle.internal.dsl.DefaultConfig
import io.wax911.emoji.buildSrc.plugin.extensions.*
import io.wax911.emoji.buildSrc.plugin.extensions.baseAppExtension
import io.wax911.emoji.buildSrc.plugin.extensions.baseExtension
import io.wax911.emoji.buildSrc.plugin.extensions.spotlessExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.io.File

internal fun Project.configureSpotless() {
    if (isLibraryModule())
        spotlessExtension().run {
            kotlin {
                target("**/kotlin/**/*.kt")
                targetExclude("$buildDir/**/*.kt", "bin/**/*.kt")
                ktlint(Versions.ktlint).userData(
                    mapOf("android" to "true")
                )
                licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
            }
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
    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName
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
        excludes.add("META-INF/NOTICE.txt")
        excludes.add("META-INF/LICENSE")
        excludes.add("META-INF/LICENSE.txt")
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

    lintOptions {
        isAbortOnError = false
        isIgnoreWarnings = false
        isIgnoreTestSources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
            allWarningsAsErrors = false
            kotlinOptions {
                allWarningsAsErrors = false
                val compileArgs = mutableListOf("-Xopt-in=kotlin.Experimental")
                if (isSampleModule()) {
                    compileArgs.add("-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
                    compileArgs.add("-Xopt-in=kotlinx.coroutines.FlowPreview")
                    compileArgs.add("-Xopt-in=kotlin.Experimental")
                } else
                    compileArgs.add("-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi")
                // Filter out modules that won't be using coroutines
                freeCompilerArgs = compileArgs
            }
        }
    }

    tasks.withType(KotlinJvmCompile::class.java) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}