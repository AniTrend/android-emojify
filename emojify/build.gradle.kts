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