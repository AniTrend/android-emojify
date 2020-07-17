rootProject.name= "android-emojify"
include(":emojify")

if (!System.getenv().containsKey("CI"))
    include(":app")