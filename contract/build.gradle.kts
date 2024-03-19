plugins {
    id("io.wax911.emojify")
}

android {
    namespace = "io.wax911.emojify.contract"
}

dependencies {
    api(libs.androidx.startup.runtime)
}
