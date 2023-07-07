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

import com.adarshr.gradle.testlogger.theme.ThemeType
import io.klogging.build.configureAssemble
import io.klogging.build.configurePublishing
import io.klogging.build.configureTesting
import io.klogging.build.configureVersioning
import io.klogging.build.configureWrapper

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.versionCatalogUpdate)
    alias(libs.plugins.testLogger)
}

group = "io.klogging"

repositories {
    mavenCentral()
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

testlogger {
    theme = ThemeType.STANDARD
    showExceptions = true
    showStackTraces = false
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showOnlySlow = false
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
}

configureAssemble()
configurePublishing()
configureTesting()
configureVersioning()
configureWrapper()
