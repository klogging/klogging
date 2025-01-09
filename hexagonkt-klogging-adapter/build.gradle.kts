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
        // Minimum Java version for Hexagon
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    explicitApi()

    jvmToolchain {
        // Minimum Java version for Hexagon
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    sourceSets.all {
        languageSettings.apply {
            languageVersion = "1.8"
            apiVersion = "1.6"
        }
    }
}

dependencies {
    // Match the dependency version to the current one.
    api("io.klogging:slf4j-klogging:${project.version}")
    implementation(libs.hexagon.core)

    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.property)
    testImplementation(libs.html.reporter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testlogger {
    showPassed = false
    showSkipped = true
    showFailed = true
}

// Create a publication to sign to publish.
publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["kotlin"])
            artifact(tasks.named("sourcesJar"))
            pom {
                name.set("hexagonkt-klogging-adapter")
                description.set("Hexagon logging adapter implemented with Klogging logging library")
            }
        }
    }
}
