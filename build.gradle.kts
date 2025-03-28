plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false

    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.jb) apply false

    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.publish) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.compatibility) apply false
}

allprojects {
    version = "1.0.0-alpha02"
    group = "dev.zt64.compose.pipette"
}