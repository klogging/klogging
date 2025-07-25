/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

plugins {
    id("klogging-kotlin-jvm")
    id("klogging-spotless")
    id("klogging-publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kover)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    sourceSets.all {
        languageSettings.apply {
            languageVersion = "2.2"
            apiVersion = "1.8"
        }
    }
}

dependencies {
    // Match the dependency version to the current one.
    api("io.klogging:klogging-jvm:${project.version}")
    api(libs.slf4j)

    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.html.reporter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testlogger {
    showPassed = false
    showSkipped = true
    showFailed = true
}

java {
    withJavadocJar()
}

val javadocJar =
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaHtml"))
    }

// Create a publication to sign to publish.
publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["kotlin"])
            artifact(javadocJar)
            artifact(tasks.named("sourcesJar"))
            pom {
                name.set("slf4j-klogging")
                description.set("SLF4J provider implemented with Klogging logging library")
            }
        }
    }
}
