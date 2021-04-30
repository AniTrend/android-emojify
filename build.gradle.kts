// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(io.wax911.emoji.buildSrc.Libraries.Android.Tools.buildGradle)
        classpath(io.wax911.emoji.buildSrc.Libraries.JetBrains.Kotlin.Gradle.plugin)
        classpath(io.wax911.emoji.buildSrc.Libraries.JetBrains.Kotlin.Serialization.serialization)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
