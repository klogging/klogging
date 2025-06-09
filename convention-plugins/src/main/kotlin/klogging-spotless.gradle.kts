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
    id("com.diffplug.spotless")
}

val licenceText = """
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
"""

val kotlinLicenceHeader = """/*
$licenceText
*/

"""

val ktlintVersion = "1.5.0"

spotless {
    format("markdown") {
        target("**/*.md")
        targetExclude(".gradle/**", ".gradle-cache/**", "build/**")
        leadingTabsToSpaces()
        endWithNewline()
    }

    format("misc") {
        target("**/.gitignore", "**/*.yaml", "**/*.yml", "**/*.sh", "**/Dockerfile")
        targetExclude("**/*.md", ".gradle/**", ".gradle-cache/**", "build/**")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.gradle.kts")

//        diktat()
        ktlint(ktlintVersion)

        licenseHeader(
            kotlinLicenceHeader,
            "@file|import|tasks|apply|plugins|rootProject|dependencyResolutionManagement",
        )

        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }

    kotlin {
        target("src/**/*.kt")

        ktlint(ktlintVersion)

        licenseHeader(kotlinLicenceHeader)

        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }

    // Only apply Diktat rules to production code.
    kotlin {
        target("src/main/**/*.kt")
        // Diktat tries to change overridden SLF4J method `getMDCAdapter()`
        targetExclude("**/KloggingServiceProvider.kt")
        diktat()
    }

    java {
        target("src/**/*.java")

        licenseHeader(kotlinLicenceHeader)

        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}

tasks.named("spotlessKotlinCheck") {
    mustRunAfter(tasks.withType<Test>().names)
}
