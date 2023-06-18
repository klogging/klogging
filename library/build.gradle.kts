/*

   Copyright 2021-2023 Michael Strasser.

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

import io.klogging.build.configureJacoco
import io.klogging.build.configureSpotless
import io.klogging.build.configureTesting

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompatibilityValidator)
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("8"))
    }

    jvm {
        withJava() // Needed for jacocoTestReport Gradle target
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        nodejs()
    }

    sourceSets.all {
        languageSettings.apply {
            languageVersion = "1.8"
            apiVersion = "1.6"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.serialisation.json)
                implementation(libs.kotlin.atomicfu)
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(libs.kotlin.serialisation.hocon)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.junit)
                implementation(libs.kotest.datatest)
                implementation(libs.kotest.xml)
                implementation(libs.kotest.property)
                implementation(libs.html.reporter)
            }
        }
        val jsMain by getting
        val jsTest by getting
    }
}

tasks.dokkaHtml.configure {
    moduleName.set("Klogging")
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(true)
            includes.from("src/commonMain/kotlin/packages.md")
        }
    }
}

configureJacoco(libs.versions.jacoco.get())
configureSpotless(libs.versions.ktlint.get())
configureTesting()
