package io.wax911.emoji.buildSrc

import io.wax911.emoji.buildSrc.common.Versions
import io.wax911.emoji.buildSrc.module.Modules

object Libraries {
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val junit = "junit:junit:${Versions.junit}"

    object Android {

        object Tools {
            private const val version = "7.2.1"
            const val buildGradle = "com.android.tools.build:gradle:$version"
        }
    }

    object AndroidX {

        object Core {
            private const val version = "1.8.0"
            const val core = "androidx.core:core:$version"
            const val coreKtx = "androidx.core:core-ktx:$version"
        }

        object ConstraintLayout {
            private const val version = "2.1.4"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$version"
        }

        object Emoji {
            private const val version = "1.1.0"
            const val emoji = "androidx.emoji2.emoji2:emoji2:$version"
            const val bundled = "androidx.emoji2.emoji2:emoji2-bundled:$version"
            const val views = "androidx.emoji2.emoji2:emoji2-views:$version"
            const val viewsHelper = "androidx.emoji2.emoji2:emoji2-views-helper:$version"
        }

        object Lifecycle {
            private const val version = "2.5.0"
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
            private const val version = "1.1.1"
            const val startUpRuntime = "androidx.startup:startup-runtime:$version"
        }

        object Test {
            private const val version = "1.4.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            object Extension {
                private const val version = "1.1.3"
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
            private const val version = "0.8.0"
            const val turbine = "app.cash.turbine:turbine:$version"
        }
    }

    object Google {

        object Material {
            private const val version = "1.6.1"
            const val material = "com.google.android.material:material:$version"
        }
    }

    object JetBrains {

        object Kotlin {
            private const val version = Versions.kotlin
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
                private const val version = "1.6.4"
                const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
                const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
                const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            }

            object Serialization {
                private const val version = "1.3.3"
                const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            }
        }
    }

    object Mockk {
        private const val version = "1.12.4"
        const val mockk = "io.mockk:mockk:$version"
        const val mockkAndroid = "io.mockk:mockk-android:$version"
    }
}