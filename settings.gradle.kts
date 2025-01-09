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

rootProject.name = "klogging"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()

        maven("https://s01.oss.sonatype.org/content/repositories/releases/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

includeBuild("convention-plugins")

include(
    "klogging",
    "slf4j-klogging",
    "klogging-spring-boot-starter",
    "jdk-platform-klogging",
    "hexagonkt-klogging-adapter",
)

// Reckon plugin to set version based on Git tags.
plugins {
    id("org.ajoberstar.reckon.settings") version "0.19.1"
}
extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("minor")
    snapshots()
    setStageCalc(calcStageFromProp())
    setScopeCalc(calcScopeFromProp())
}
