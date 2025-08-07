import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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

kotlin {
    explicitApi()

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
    }

    java {
        compilerOptions {
            // JDK Platform logging was introduced in JDK 9
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

dependencies {
    // Match the dependency version to the current one.
    api("io.klogging:klogging-jvm:${project.version}")

    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.property)
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
            artifact(tasks.named("sourcesJar"))
            artifact(javadocJar)
            pom {
                name.set("jdk-platform-klogging")
                description.set("JDK Platform System.Logger implemented with Klogging logging library")
            }
        }
    }
}
