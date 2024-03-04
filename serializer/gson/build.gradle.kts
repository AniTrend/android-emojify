import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

android {
    namespace = "io.wax911.emojify.serializer.gson"
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.contract))
    api(libs.gson)
}
