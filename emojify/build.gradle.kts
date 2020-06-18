import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
    id("kotlinx-serialization")
}

dependencies {
    implementation(Libraries.Google.Gson.gson)
    implementation(Libraries.JetBrains.KotlinX.Serialization.runtime)
}