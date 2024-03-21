import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

android {
    namespace = "io.wax911.emojify.initializer"
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.contract))
    implementation(project(Libraries.AniTrend.Emojify.emojify))
    api(libs.androidx.startup.runtime)
}
