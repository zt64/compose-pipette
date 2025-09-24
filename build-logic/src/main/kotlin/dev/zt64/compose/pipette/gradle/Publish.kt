package dev.zt64.compose.pipette.gradle

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure

fun Project.publishing(libName: String) {
    apply(plugin = "com.vanniktech.maven.publish")

    configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        coordinates("dev.zt64.compose.pipette", libName, project.version.toString())
        signAllPublications()

        pom {
            name = libName
            description = project.description
            inceptionYear = "2024"
            url = "https://github.com/zt64/compose-pipette"

            developers {
                developer {
                    id = "zt64"
                    name = "zt64"
                    email = "31907977+zt64@users.noreply.github.com"
                }
            }

            licenses {
                license {
                    name = "MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }

            scm {
                url = "https://github.com/zt64/compose-pipette"
                connection = "scm:git:git://github.com/zt64/compose-pipette.git"
                developerConnection = "scm:git:ssh://git@github.com/zt64/compose-pipette.git"
            }
        }
    }
}