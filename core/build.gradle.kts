import dev.zt64.compose.pipette.gradle.apple
import dev.zt64.compose.pipette.gradle.publishing
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-library")
    alias(libs.plugins.compatibility)
}

description = "Color pickers for Kotlin Compose multiplatform"

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    jvm()
    apple()

    wasmJs {
        browser()
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