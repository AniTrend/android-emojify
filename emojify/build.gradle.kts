import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
    id("kotlinx-serialization")
}

dependencies {
    implementation(Libraries.AndroidX.Core.coreKtx)
    implementation(Libraries.AndroidX.StartUp.startUpRuntime)
    implementation(Libraries.JetBrains.KotlinX.Serialization.json)
}