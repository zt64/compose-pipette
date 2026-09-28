import dev.zt64.compose.pipette.gradle.apple
import dev.zt64.compose.pipette.gradle.publishing
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-library")
    alias(libs.plugins.compatibility)
    alias(libs.plugins.android.mp.library)
}

description = "Color pickers for Kotlin Compose multiplatform"

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    jvm()
    apple()

    android {
        namespace = "dev.zt64.compose.pipette"
        compileSdk = 37
        minSdk = 24

        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    wasmJs {
        browser {
            testTask {
                enabled = false // Test complains about browser too much
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.compose.ui.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

publishing("compose-pipette")