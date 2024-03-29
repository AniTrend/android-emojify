import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
    id("kotlinx-serialization")
}

android {
    namespace = "io.wax911.emojify.serializer.kotlinx"
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.contract))
    api(libs.jetbrains.kotlinx.serialization.json)
}
