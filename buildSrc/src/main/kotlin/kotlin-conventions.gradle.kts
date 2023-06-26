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

plugins {
    kotlin("multiplatform")
    id("com.adarshr.test-logger")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

testlogger {
    showPassed = false
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
}

kotlin {
    jvmToolchain(8)

    sourceSets.configureEach {
        languageSettings {
            optIn("kotlin.time.ExperimentalTime")
            optIn("kotlin.experimental.ExperimentalTypeInference")
            optIn("kotlin.contracts.ExperimentalContracts")
        }
    }

    sourceSets.all {
        languageSettings.apply {
            languageVersion = "1.8"
            apiVersion = "1.6"
        }
    }
}
