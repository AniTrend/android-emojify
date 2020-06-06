rootProject.name= "AndroidEmojify"
include(":emojify")

if (!System.getenv().containsKey("CI"))
    include(":app")