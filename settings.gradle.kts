rootProject.name = "android-emojify"
include(":emojify")
include(":contract")
include(":initializer")
include(":serializer:kotlinx")
include(":serializer:gson")
include(":serializer:moshi")

if (!System.getenv().containsKey("CI"))
    include(":app")
