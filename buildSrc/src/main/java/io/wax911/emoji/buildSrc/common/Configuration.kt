package io.wax911.emoji.buildSrc.common

object Configuration {

    private fun Int.toVersion(): String {
        return if (this < 9) "0$this" else "$this"
    }

    const val compileSdk = 32
    const val targetSdk = 32
    const val minSdk = 21

    private const val major = 1
    private const val minor = 7
    private const val patch = 0
    private const val revision = 0

    private const val channel = "rc"

    const val versionCode = major.times(1_000_000_000) +
            minor.times(1_000_000) +
            patch.times(1_000) +
            revision

    val versionName = if (revision > 1)
        "$major.$minor.$patch-${channel}${revision.toVersion()}"
    else
        "$major.$minor.$patch"
}