/*

   Copyright 2021-2022 Michael Strasser.

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

import io.klogging.build.configureAssemble
import io.klogging.build.configureJacoco
import io.klogging.build.configurePublishing
import io.klogging.build.configureSpotless
import io.klogging.build.configureTesting
import io.klogging.build.configureWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.versions)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versionCatalogUpdate)
}

group = "io.klogging"
version = "0.5.0-SNAPSHOT"

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
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("8"))
    }

    jvm {
        withJava() // Needed for jacocoTestReport Gradle target
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(LEGACY) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.serialisation.json)
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
                implementation(libs.kotest.xml)
                implementation(libs.kotest.datatest)
                implementation(libs.html.reporter)
            }
        }
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

// Explicitly set a file copying strategy so `klogging.json` in the jvmTest module
// will copy correctly for tests to run.
// This might be a workaround for a bug in Gradle Kotlin scripts, see:
// https://youtrack.jetbrains.com/issue/KT-46165
tasks.withType<ProcessResources> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        // Opt in for kotlinx-datetime features (see `Timestamps.kt`)
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
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

configureAssemble()
configureJacoco(libs.versions.jacoco.get())
configurePublishing()
configureSpotless(libs.versions.ktlint.get())
configureTesting()
configureWrapper()
