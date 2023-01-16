import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `kotlin-dsl`
    `maven-publish`
    `version-catalog`
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://www.jitpack.io")
    }
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

tasks.withType(KotlinJvmCompile::class.java) {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val libs = extensions.getByType<LibrariesForLibs>()

dependencies {
    /** Depend on the android gradle plugin, since we want to access it in our plugin */
    implementation(libs.android.gradle.plugin)

    /** Depend on the kotlin plugin, since we want to access it in our plugin */
    implementation(libs.jetbrains.kotlin.gradle)

    /** Depend on the dokka plugin, since we want to access it in our plugin */
    implementation(libs.jetbrains.dokka.gradle)

    /** Dependency management */
    implementation(libs.gradle.versions)

    /** Spotless */
    implementation(libs.spotless.gradle)

    /** Depend on the default Gradle API's since we want to build a custom plugin */
    implementation(gradleApi())
    implementation(localGroovy())

    /** Work around to include ../.gradle/LibrariesForLibs generated file for version catalog */
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}