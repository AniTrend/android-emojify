rootProject.name = "android-emojify"

dependencyResolutionManagement {
    versionCatalogs {
        // The default libs catalog is auto-derived from gradle/libs.versions.toml and must not
        // be re-declared here (Gradle 9 rejects a second 'from' invocation on the same catalog).
        create("sampleLibs") {
            from(files("gradle/sample.versions.toml"))
        }
    }
}

include(":emojify")
include(":contract")
include(":serializer:kotlinx")
include(":serializer:gson")
include(":serializer:moshi")

if (!System.getenv().containsKey("CI"))
    include(":app")
