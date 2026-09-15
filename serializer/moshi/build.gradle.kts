import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.wax911.emojify.serializer.moshi"
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.contract))
    api(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
}
