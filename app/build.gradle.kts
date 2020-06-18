import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

dependencies {
    implementation(project(":emojify"))

    implementation(Libraries.Google.Material.material)
    implementation(Libraries.AndroidX.ContraintLayout.constraintLayout)
}
