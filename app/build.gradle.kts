import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.emojify))
    implementation(project(Libraries.AniTrend.Emojify.contract))
    implementation(project(Libraries.AniTrend.Emojify.serializerKotlinx))

    implementation(sampleLibs.google.android.material)
    implementation(sampleLibs.androidx.constraintlayout)

    // Coroutines are supplied by DependencyStrategy (sample-only branch), do not redeclare.
    implementation(libs.androidx.startup.runtime)
}

android {
    namespace = "io.wax911.emojifysample"
}
