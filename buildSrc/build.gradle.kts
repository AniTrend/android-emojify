plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven {
        setUrl("https://www.jitpack.io")
    }
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

val kotlinVersion = "1.5.31"
val buildToolsVersion = "4.1.3"
val dokkaVersion = "1.4.30"
val manesVersion = "0.33.0"
val spotlessVersion = "5.12.1"

dependencies {
    /* Depend on the android gradle plugin, since we want to access it in our plugin */
    implementation("com.android.tools.build:gradle:$buildToolsVersion")

    /* Depend on the kotlin plugin, since we want to access it in our plugin */
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    /* Depend on the dokka plugin, since we want to access it in our plugin */
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")

    /** Dependency management */
    implementation("com.github.ben-manes:gradle-versions-plugin:$manesVersion")

    /** Spotless */
    implementation("com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion")

    /* Depend on the default Gradle API's since we want to build a custom plugin */
    implementation(gradleApi())
    implementation(localGroovy())
}