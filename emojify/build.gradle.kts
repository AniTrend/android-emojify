import io.wax911.emoji.buildSrc.Libraries

plugins {
    id("io.wax911.emojify")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
    implementation(project(Libraries.AniTrend.Emojify.contract))
}

android {
    namespace = "io.wax911.emojify"
}

tasks.register<Copy>("preTest") {
    from("src/main/assets/emoticons/")
    into("src/test/resources/io/wax911/emojify/core/")
}

tasks.register<Delete>("postTest") {
    delete(
        fileTree("src/test/resources/io/wax911/emojify/core/") {
            include("emoji.json")
        },
    )
}
