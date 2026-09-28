import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-base")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.android.mp.library)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    android {
        namespace = "dev.zt64.compose.pipette.sample"
        compileSdk = 37
        minSdk = 24
    }

    wasmJs {
        outputModuleName = "sample"
        browser {
            commonWebpackConfig {
                outputFileName = "sample.js"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core)

                implementation(libs.compose.runtime)
                implementation(libs.compose.material3)
                implementation(libs.compose.materialIcons.core)

                implementation(libs.materialKolor)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose {
    resources {
        generateResClass = ResourcesExtension.ResourceClassGeneration.Never
    }

    desktop {
        application {
            mainClass = "dev.zt64.compose.pipette.sample.MainKt"
        }
    }
}