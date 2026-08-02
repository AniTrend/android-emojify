import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

dependencies {
    implementation(project(Libraries.AniTrend.Emojify.emojify))
    implementation(project(Libraries.AniTrend.Emojify.contract))
    implementation(project(Libraries.AniTrend.Emojify.serializerKotlinx))

    implementation(platform(sampleLibs.androidx.compose.bom))
    implementation(sampleLibs.androidx.compose.material3)
    implementation(sampleLibs.androidx.compose.ui)
    implementation(sampleLibs.androidx.compose.ui.graphics)
    implementation(sampleLibs.androidx.compose.ui.tooling.preview)
    implementation(sampleLibs.androidx.activity.compose)

    debugImplementation(sampleLibs.androidx.compose.ui.test.manifest)
    debugImplementation(sampleLibs.androidx.compose.ui.tooling)

    androidTestImplementation(platform(sampleLibs.androidx.compose.bom))
    androidTestImplementation(sampleLibs.androidx.compose.ui.test.junit4)

    // Coroutines are supplied by DependencyStrategy (sample-only branch), do not redeclare.
    implementation(libs.androidx.startup.runtime)
}

android {
    namespace = "io.wax911.emojifysample"
}
