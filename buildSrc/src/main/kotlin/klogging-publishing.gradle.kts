import java.nio.file.Files
import java.util.Base64

plugins {
    signing
    `java-library`
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin")
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

val javadoc = tasks.named("javadoc")

group = "io.klogging"

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles java doc to jar"
    archiveClassifier.set("javadoc")
    from(javadoc)
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.apply {
            artifact(javadocJar)
        }
    }
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKeyId: String by project
val signingKey: String by project
val signingPassword: String by project

signing {
    val keyId = System.getenv("SIGNING_KEY_ID") ?: signingKeyId
    val keyRing = System.getenv("SIGNING_KEY") ?: signingKey
    val keyPassphrase = System.getenv("SIGNING_PASSWORD") ?: signingPassword

    val keyRingFilePath = Files.createTempFile("klogging-signing", ".gpg")
    keyRingFilePath.toFile().deleteOnExit()

    Files.write(keyRingFilePath, Base64.getDecoder().decode(keyRing))

    project.extra["signing.keyId"] = keyId
    project.extra["signing.secretKeyRingFile"] = keyRingFilePath.toString()
    project.extra["signing.password"] = keyPassphrase

//    useGpgCmd()
//    val key = System.getenv("SIGNING_KEY") ?: signingKey
//    val pass = System.getenv("SIGNING_PASSWORD") ?: signingPassword
//    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publications)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME") ?: ossrhUsername)
            password.set(System.getenv("OSSRH_PASSWORD") ?: ossrhPassword)
        }
    }
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.apply {
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
}
