package io.wax911.emoji.buildSrc

import io.wax911.emoji.buildSrc.common.Versions

object Libraries {
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val junit = "junit:junit:${Versions.junit}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"

    object Android {

        object Tools {
            private const val version = "4.0.0"
            const val buildGradle = "com.android.tools.build:gradle:$version"
        }
    }

    object AndroidX {

        object Activity {
            private const val version = "1.1.0"
            const val activity = "androidx.activity:activity:$version"
            const val activityKtx = "androidx.activity:activity-ktx:$version"
        }

        object AppCompat {
            private const val version = "1.1.0"
            const val appcompat = "androidx.appcompat:appcompat:$version"
            const val appcompatResources = "androidx.appcompat:appcompat-resources:$version"
        }

        object Collection {
            private const val version = "1.1.0"
            const val collection = "androidx.collection:collection:$version"
            const val collectionKtx = "androidx.collection:collection-ktx:$version"
        }

        object Core {
            private const val version = "1.3.0"
            const val core = "androidx.core:core:$version"
            const val coreKtx = "androidx.core:core-ktx:$version"

            object Animation {
                private const val version = "1.0.0-alpha01"
                const val animation = "androidx.core:core-animation:$version"
                const val animationTest = "androidx.core:core-animation-testing:$version"
            }
        }

        object ContraintLayout {
            private const val version = "2.0.0-beta6"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$version"
            const val constraintLayoutSolver = "androidx.constraintlayout:constraintlayout-solver:$version"
        }

        object Emoji {
            private const val version = "1.1.0-rc01"
            const val appCompat = "androidx.emoji:emoji-appcompat:$version"
        }

        object Fragment {
            private const val version = "1.2.3"
            const val fragment = "androidx.fragment:fragment:$version"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
        }

        object Lifecycle {
            private const val version = "2.2.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val runTimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:$version"
        }

        object Navigation {
            private const val version = "2.3.0-beta01"
            const val common = "androidx.navigation:navigation-common:$version"
            const val commonKtx = "androidx.navigation:navigation-common-ktx:$version"

            const val dynamicFragment = "androidx.navigation:navigation-dynamic-features-fragment:$version"
            const val dynamicRuntime = "androidx.navigation:navigation-dynamic-features-runtime:$version"

            const val fragment = "androidx.navigation:navigation-fragment:$version"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"

            const val runtime = "androidx.navigation:navigation-runtime:$version"
            const val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:$version"

            const val safeArgsgenerator = "androidx.navigation:navigation-safe-args-generator:$version"
            const val safeArgsGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"

            const val test = "androidx.navigation:navigation-testing:$version"

            const val ui = "androidx.navigation:navigation-ui:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object Paging {
            private const val version = "2.1.2"
            const val common = "androidx.paging:paging-common-ktx:$version"
            const val runtime = "androidx.paging:paging-runtime:$version"
            const val runtimeKtx = "androidx.paging:paging-runtime-ktx:$version"
        }

        object Preference {
            private const val version = "1.1.0"
            const val preference = "androidx.preference:preference:$version"
            const val preferenceKtx = "androidx.preference:preference-ktx:$version"
        }

        object Recycler {
            // TODO: Downgrade when material integrates merge adapter and state restoration
            private const val version = "1.2.0-alpha03"
            const val recyclerView = "androidx.recyclerview:recyclerview:$version"
            const val recyclerViewSelection = "androidx.recyclerview:recyclerview-selection:$version"
        }

        object Room {
            private const val version = "2.2.5"
            const val compiler = "androidx.room:room-compiler:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val test = "androidx.room:room-testing:$version"
            const val ktx = "androidx.room:room-ktx:$version"
        }

        object Slice {
            private const val version = "1.0.0"
            const val core = "androidx.slice:slice-core:$version"
        }

        object SwipeRefresh {
            private const val version = "1.1.0-rc01"
            const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:$version"
        }

        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            object Espresso {
                private const val version = "3.3.0-rc01"
                const val core = "androidx.test.espresso:espresso-core:$version"
            }

            object Extension {
                private const val version = "1.1.1"
                const val junit = "androidx.test.ext:junit:$version"
                const val junitKtx = "androidx.test.ext:junit-ktx:$version"
            }
        }

        object Work {
            private const val version = "2.4.0-beta01"
            const val runtimeKtx = "androidx.work:work-runtime-ktx:$version"
            const val runtime = "androidx.work:work-runtime:$version"
            const val test = "androidx.work:work-test:$version"
        }
    }

    object Google {

        object Firebase {
            private const val version = "17.4.3"
            const val firebaseCore = "com.google.firebase:firebase-core:$version"

            object Analytics {
                private const val version = "17.4.3"
                const val analytics = "com.google.firebase:firebase-analytics:$version"
                const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx:$version"
            }

            object Crashlytics {
                private const val version = "17.0.1"
                const val crashlytics = "com.google.firebase:firebase-crashlytics:$version"

                object Gradle {
                    private const val version = "2.1.1"
                    const val plugin = "com.google.firebase:firebase-crashlytics-gradle:$version"
                }
            }
        }

        object Gson {
            private const val version = "2.8.6"
            const val gson = "com.google.code.gson:gson:$version"
        }

        object Material {
            private const val version = "1.2.0-beta01"
            const val material = "com.google.android.material:material:$version"
        }

        object Services {
            private const val version = "4.3.3"
            const val googleServices = "com.google.gms:google-services:$version"
        }
    }

    object JetBrains {
        object Coroutines {
            private const val version = "1.3.7"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }

        object Dokka {
            private const val version = "0.10.1"
            const val gradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:$version"
        }

        object Kotlin {
            private const val version = "1.3.72"
            const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

            object Gradle {
                const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
            }

            object Android {
                const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
            }
        }
    }
}