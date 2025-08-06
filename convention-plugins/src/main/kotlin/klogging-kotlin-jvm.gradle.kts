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
    kotlin("jvm")
}

kotlin {
    explicitApi()

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
    }

    java {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)

            // Generate default methods for implementations in interfaces.
            // Not needed from Kotlin 2.2? See https://youtrack.jetbrains.com/issue/KT-71768
            freeCompilerArgs.add("-Xjvm-default=all-compatibility")
            // From https://www.liutikas.net/2025/01/10/Conservative-Librarian.html
            // Needed due to https://youtrack.jetbrains.com/issue/KT-49746
            freeCompilerArgs.add("-Xjdk-release=1.8")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}
