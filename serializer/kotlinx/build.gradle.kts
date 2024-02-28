import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
}

android {
    namespace = "io.wax911.emojify.serializer"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.contract))
    api(libs.jetbrains.kotlinx.serialization.json)
}
