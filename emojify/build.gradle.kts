import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

plugins {
    id("io.wax911.emojify")
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.jetbrains.kotlinx.serialization.json)
}

android {
    namespace = "io.wax911.emojify"
}

tasks.register<Copy>("preTest") {
    from("src/main/assets/emoticons/")
    into("src/test/resources/io/wax911/emojify/core/")
}

tasks.register<Delete>("postTest") {
    delete(fileTree("src/test/resources/io/wax911/emojify/core/") {
        include("emoji.json")
    })
}
