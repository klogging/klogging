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

plugins {
    kotlin("jvm") version "1.5.21"
    `java-library`
}

val jacocoVersion: String by project
val kotestVersion: String by project
val ktlintVersion: String by project

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, "minutes")
    resolutionStrategy.cacheChangingModulesFor(10, "minutes")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("io.klogging:klogging-jvm:0.3.0-SNAPSHOT")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

configureAssemble()
configureJacoco(jacocoVersion)
configurePublishing()
configureSpotless(ktlintVersion)
configureTesting()
configureVersioning()
configureWrapper()
