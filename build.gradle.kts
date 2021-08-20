/*

   Copyright 2021 Michael Strasser.

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
import io.klogging.build.configureVersioning
import io.klogging.build.configureWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.ben-manes.versions")
}

group = "io.klogging"
version = "0.4.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val jacocoVersion: String by project
val kotlinCoroutinesVersion: String by project
val kotlinDateTimeVersion: String by project
val kotlinSerialisationJsonVersion: String by project
val kotestVersion: String by project
val ktlintVersion: String by project

kotlin {
    explicitApi()

    jvm {
        withJava() // Needed for jacocoTestReport Gradle target

        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinDateTimeVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerialisationJsonVersion")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
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

configureAssemble()
configureJacoco(jacocoVersion)
configurePublishing()
configureSpotless(ktlintVersion)
configureTesting()
configureVersioning()
configureWrapper()
