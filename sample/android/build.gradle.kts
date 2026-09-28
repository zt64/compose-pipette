import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.android.application)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    android {
        namespace = "dev.zt64.compose.pipette.sample.android"
        compileSdk = 37

        defaultConfig {
            minSdk = 24
            targetSdk = 37
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            }
        }
    }

    dependencies {
        implementation(projects.sample.shared)
        implementation(libs.androidx.activity)
        implementation(libs.appcompat)
    }
}