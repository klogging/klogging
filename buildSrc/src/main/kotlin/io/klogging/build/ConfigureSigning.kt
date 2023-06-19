package io.klogging.build

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.nio.file.Files
import java.util.Base64

fun Project.configureSigning() {
    apply<SigningPlugin>()

    configure<SigningExtension> {
        sign(publishing.publications)
    }

    tasks.withType<Sign>().configureEach {
        doFirst {
            val keyId = getEnvironmentVariableOrThrow("SIGNING_KEY_ID")
            val keyRing = getEnvironmentVariableOrThrow("SIGNING_KEY")
            val keyPassphrase = getEnvironmentVariableOrThrow("SIGNING_PASSWORD")

            val keyRingFilePath = Files.createTempFile("klogging-signing", ".gpg")
            keyRingFilePath.toFile().deleteOnExit()

            Files.write(keyRingFilePath, Base64.getDecoder().decode(keyRing))

            project.extra["signing.keyId"] = keyId
            project.extra["signing.secretKeyRingFile"] = keyRingFilePath.toString()
            project.extra["signing.password"] = keyPassphrase
        }
    }
}