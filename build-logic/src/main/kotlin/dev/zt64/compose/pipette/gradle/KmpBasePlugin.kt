package dev.zt64.compose.pipette.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureKmp(target)
    }

    private fun configureKmp(target: Project) {
        target.apply(plugin = "org.jetbrains.kotlin.multiplatform")
        target.configure<KotlinMultiplatformExtension> {
            jvmToolchain(17)

            jvm()
            apple()
            js {
                browser {
                    testTask {
                        // See: https://youtrack.jetbrains.com/issue/CMP-4906/1nMakeRasterN32Premul-is-not-defined-when-running-runComposeUiTest-with-js
                        enabled = false
                    }
                }
                // Fix for https://youtrack.jetbrains.com/issue/CMP-4906, TODO: Remove once compose v1.13.0 goes stable
                binaries.executable()
            }
        }
    }
}