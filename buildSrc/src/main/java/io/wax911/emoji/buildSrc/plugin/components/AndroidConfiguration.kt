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
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.File

internal fun Project.configureSpotless() {
    if (isLibraryModule())
        spotlessExtension().run {
            kotlin {
                target("**/kotlin/**/*.kt")
                targetExclude("${layout.buildDirectory.get()}/**/*.kt", "**/test/**/*.kt", "bin/**/*.kt")
                ktlint(libs.versions.ktlint.get())
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
            viewBinding = true
        }
        println("Applying vector drawables configuration for module -> ${project.path}")
        vectorDrawables.useSupportLibrary = true
    } else
        consumerProguardFiles.add(File("consumer-rules.pro"))
}

internal fun Project.configureAndroid(): Unit = baseExtension().run {
    compileSdkVersion(34)
    defaultConfig {
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType(KotlinJvmCompile::class.java) {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
            allWarningsAsErrors = false
            kotlinOptions {
                allWarningsAsErrors = false
                val compileArgs = mutableListOf<String>()
                if (isSampleModule()) {
                    compileArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
                    compileArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
                }
                freeCompilerArgs = compileArgs
            }
        }
    }
}
