rootProject.name = "android-emojify"
include(":emojify")
include(":contract")
include(":serializer:kotlinx")
include(":serializer:gson")
include(":serializer:moshi")

if (!System.getenv().containsKey("CI"))
    include(":app")
