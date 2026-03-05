import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-base")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.android.application)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    androidTarget()

    wasmJs {
        outputModuleName = "sample"
        browser {
            commonWebpackConfig {
                outputFileName = "sample.js"
            }
        }
        binaries.executable()
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

        androidMain {
            dependencies {
                implementation(libs.androidx.activity)
                implementation(libs.appcompat)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "dev.zt64.compose.pipette.sample"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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