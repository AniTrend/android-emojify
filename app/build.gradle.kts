import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.emojify))

    implementation(libs.google.android.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.jetbrains.kotlinx.coroutines.core)

    implementation(libs.androidx.startup.runtime)
}
