package dev.zt64.compose.pipette.gradle

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Locale

/**
 * Registers the Apple native targets, but only on macOS hosts since Kotlin/Native
 * cannot cross-compile Apple targets from other operating systems.
 */
fun KotlinMultiplatformExtension.apple(configure: KotlinNativeTarget.() -> Unit = {}) {
    val isMacOs = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")

    if (!isMacOs) return

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64()
    ).forEach(configure)
}
