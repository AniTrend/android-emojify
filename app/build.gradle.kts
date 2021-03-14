import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

android {

}

dependencies {
    implementation(project(":emojify"))

    implementation(Libraries.Google.Material.material)
    implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

    implementation(Libraries.JetBrains.KotlinX.Coroutines.android)
    implementation(Libraries.JetBrains.KotlinX.Coroutines.core)

    implementation(Libraries.AndroidX.StartUp.startUpRuntime)
}
