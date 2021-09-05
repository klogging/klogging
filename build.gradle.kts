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
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "io.klogging"
version = "0.1.2-SNAPSHOT"

val jacocoVersion: String by project
val kloggingVersion: String by project
val kotestVersion: String by project
val ktlintVersion: String by project
val slf4jVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api("io.klogging:klogging-jvm:$kloggingVersion")
    api("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

kotlin {
    explicitApi()
}

tasks.register<Jar>("jvmJar") {
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("jvmSourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["java"])
        }
    }
}

configureAssemble()
configureJacoco(jacocoVersion)
configurePublishing()
configureSpotless(ktlintVersion)
configureTesting()
configureVersioning()
configureWrapper()
