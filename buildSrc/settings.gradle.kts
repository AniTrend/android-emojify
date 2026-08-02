@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("sampleLibs") {
            from(files("../gradle/sample.versions.toml"))
        }
    }
}