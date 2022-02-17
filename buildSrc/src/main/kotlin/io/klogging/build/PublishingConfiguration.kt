/*

   Copyright 2022 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.build

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.nio.file.Files
import java.util.Base64

fun Project.configurePublishing() {
    apply<MavenPublishPlugin>()
    apply<NexusPublishPlugin>()
    apply<SigningPlugin>()

    val usernameEnvironmentVariableName = "OSSRH_USERNAME"
    val passwordEnvironmentVariableName = "OSSRH_PASSWORD"
    val repoUsername = System.getenv(usernameEnvironmentVariableName)
    val repoPassword = System.getenv(passwordEnvironmentVariableName)

    val validateCredentialsTask = tasks.register("validateMavenRepositoryCredentials") {
        doFirst {
            if (repoUsername.isNullOrBlank()) {
                throw RuntimeException("Environment variable '$usernameEnvironmentVariableName' not set.")
            }

            if (repoPassword.isNullOrBlank()) {
                throw RuntimeException("Environment variable '$passwordEnvironmentVariableName' not set.")
            }
        }
    }

    createPublishingTasks(repoUsername, repoPassword, validateCredentialsTask)
    createSigningTasks()
    createReleaseTasks(validateCredentialsTask)
}

private fun Project.createPublishingTasks(
    repoUsername: String?,
    repoPassword: String?,
    validateCredentialsTask: TaskProvider<Task>
) {
    configure<PublishingExtension> {
        publications.withType<MavenPublication> {
            // HACK: this is a workaround while we're waiting to get Dokka set up correctly
            // (see https://kotlinlang.slack.com/archives/C0F4UNJET/p1616470404031100?thread_ts=1616198351.029900&cid=C0F4UNJET)
            // This creates an empty JavaDoc JAR to make Maven Central happy.
            val publicationName = this.name
            val javadocTask = tasks.register<Jar>(this.name + "JavadocJar") {
                archiveClassifier.set("javadoc")
                archiveBaseName.set("klogging-$publicationName")
            }

            artifact(javadocTask)

            pom {
                name.set("klogging")
                description.set("Kotlin logging library with structured logging and coroutines support")
                url.set("https://github.com/klogging/klogging")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                    developers {
                        developer {
                            id.set("mjstrasser")
                            name.set("Michael Strasser")
                            email.set("mjstrasser@klogging.io")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/klogging/klogging.git")
                        url.set("https://github.com/klogging/klogging")
                    }
                }
            }
        }
    }

    configure<NexusPublishExtension> {
        repositories {
            sonatype {
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
                username.set(repoUsername)
                password.set(repoPassword)
            }
        }

        transitionCheckOptions {
            maxRetries.set(100)
        }
    }

    afterEvaluate {
        publishing.publications.names.forEach { publication ->
            tasks.named("publish${publication.capitalize()}PublicationToSonatypeRepository").configure {
                dependsOn(validateCredentialsTask)
            }
        }
    }
}

private fun Project.createSigningTasks() {
    configure<SigningExtension> {
        sign(publishing.publications)
    }

    tasks.withType<Sign>().configureEach {
        doFirst {
            val keyId = getEnvironmentVariableOrThrow("SIGNING_KEY_ID")
            val keyRing = getEnvironmentVariableOrThrow("SIGNING_KEY")
            val keyPassphrase = getEnvironmentVariableOrThrow("SIGNING_PASSWORD")

            val keyRingFilePath = Files.createTempFile("klogger-signing", ".gpg")
            keyRingFilePath.toFile().deleteOnExit()

            Files.write(keyRingFilePath, Base64.getDecoder().decode(keyRing))

            project.extra["signing.keyId"] = keyId
            project.extra["signing.secretKeyRingFile"] = keyRingFilePath.toString()
            project.extra["signing.password"] = keyPassphrase
        }
    }
}

private fun Project.createReleaseTasks(
    validateCredentialsTask: TaskProvider<Task>
) {
    setOf("closeSonatypeStagingRepository", "releaseSonatypeStagingRepository").forEach { taskName ->
        tasks.named(taskName).configure {
            dependsOn(validateCredentialsTask)
        }
    }

//    val validateReleaseTask = tasks.register("validateRelease") {
//        doFirst {
//            if (version.toString().contains("-")) {
//                throw RuntimeException("Attempting to publish a release of an untagged commit.")
//            }
//        }
//    }

    tasks.register("publishSnapshot") {
        dependsOn("publishJvmPublicationToSonatypeRepository")
    }

    tasks.named("closeSonatypeStagingRepository") {
        mustRunAfter("publishJvmPublicationToSonatypeRepository")
    }

    tasks.register("publishRelease") {
//        dependsOn(validateReleaseTask)
        dependsOn("publishJvmPublicationToSonatypeRepository")
        dependsOn("closeAndReleaseSonatypeStagingRepository")
    }
}

private val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

private val Project.publishing: PublishingExtension
    get() = extensions.getByType()

private fun getEnvironmentVariableOrThrow(name: String): String = System.getenv().getOrElse(name) {
    throw RuntimeException("Environment variable '$name' not set.")
}
