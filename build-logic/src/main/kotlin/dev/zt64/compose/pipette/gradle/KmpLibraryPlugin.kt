package dev.zt64.compose.pipette.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.*

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureKmp(target)
        configureCompose(target)
        configureLint(target)
        configurePublishing(target)
    }

    @OptIn(ExperimentalWasmDsl::class)
    private fun configureKmp(target: Project) {
        target.apply(plugin = "kmp-base")

        target.configure<KotlinMultiplatformExtension> {
            explicitApi()

            wasmJs {
                browser()
            }

            sourceSets {
                commonMain {
                    dependencies {
                        if (target.name != "core" && target.name != "util") {
                            implementation(target.project(":core"))
                        }

                        // implementation(compose.ui)
                        // implementation(compose.foundation)
                        // implementation(target.libs.findLibrary("compose.ui").get())
                        // implementation(target.libs.findLibrary("compose.foundation").get())
                    }
                }

                commonTest {
                    dependencies {
                        implementation(target.libs.findLibrary("kotlin.test").get())
                    }
                }
            }
        }
    }

    private fun configureCompose(target: Project) {
        target.apply {
            plugin("org.jetbrains.compose")
            plugin("org.jetbrains.kotlin.plugin.compose")
        }
    }

    private fun configureLint(target: Project) {
        target.apply(plugin = "org.jlleitschuh.gradle.ktlint")

        target.configure<KtlintExtension> {
            version.set(target.libs.findVersion("ktlint").get().requiredVersion)
        }

        target.dependencies {
            // "ktlintRuleset"(target.libs.findLibrary("ktlint-rules-compose").get().get().toString())
        }
    }

    private fun configurePublishing(target: Project) {
        target.apply(plugin = "com.vanniktech.maven.publish")
    }
}

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