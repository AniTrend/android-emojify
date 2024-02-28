rootProject.name = "android-emojify"
include(":emojify")
include(":contract")
include(":serializer:kotlinx")

if (!System.getenv().containsKey("CI"))
    include(":app")
