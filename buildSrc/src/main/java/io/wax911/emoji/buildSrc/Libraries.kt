package io.wax911.emoji.buildSrc

import io.wax911.emoji.buildSrc.common.Versions
import io.wax911.emoji.buildSrc.module.Modules

object Libraries {
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val junit = "junit:junit:${Versions.junit}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"

    object Android {

        object Tools {
            private const val version = "7.0.3"
            const val buildGradle = "com.android.tools.build:gradle:$version"
        }
    }

    object AndroidX {

        object Core {
            private const val version = "1.6.0"
            const val core = "androidx.core:core:$version"
            const val coreKtx = "androidx.core:core-ktx:$version"
        }

        object ConstraintLayout {
            private const val version = "2.1.1"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$version"
            const val constraintLayoutSolver = "androidx.constraintlayout:constraintlayout-solver:$version"
        }

        object Emoji {
            private const val version = "1.1.0"
            const val appCompat = "androidx.emoji:emoji-appcompat:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val runTimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:$version"
        }

        object Paging {
            private const val version = "3.0.0"
            const val common = "androidx.paging:paging-common-ktx:$version"
            const val runtime = "androidx.paging:paging-runtime:$version"
            const val runtimeKtx = "androidx.paging:paging-runtime-ktx:$version"
        }

        object Recycler {
            private const val version = "1.2.0"
            const val recyclerView = "androidx.recyclerview:recyclerview:$version"
            const val recyclerViewSelection = "androidx.recyclerview:recyclerview-selection:$version"
        }

        object StartUp {
            private const val version = "1.1.0"
            const val startUpRuntime = "androidx.startup:startup-runtime:$version"
        }

        object Test {
            private const val version = "1.3.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            object Extension {
                private const val version = "1.1.2"
                const val junit = "androidx.test.ext:junit:$version"
                const val junitKtx = "androidx.test.ext:junit-ktx:$version"
            }
        }
    }

    object AniTrend {

        object Emojify {
            val emojify = Modules.Library.Emojify.path()
        }
    }

    object CashApp {
        object Turbine {
            private const val version = "0.7.0"
            const val turbine = "app.cash.turbine:turbine:$version"
        }
    }

    object Google {

        object Material {
            private const val version = "1.3.0"
            const val material = "com.google.android.material:material:$version"
        }
    }

    object JetBrains {

        object Dokka {
            private const val version = "1.5.31"
            const val gradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:$version"
        }

        object Kotlin {
            private const val version = "1.5.31"
            const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

            object Gradle {
                const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
            }

            object Android {
                const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
            }

            object Serialization {
                const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$version"
            }
        }

        object KotlinX {
            object Coroutines {
                private const val version = "1.5.2"
                const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
                const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
                const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            }

            object Serialization {
                private const val version = "1.1.0"
                const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            }
        }
    }
}