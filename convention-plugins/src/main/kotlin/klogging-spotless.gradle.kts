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
    id("com.diffplug.spotless")
}

val licenceText = """
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
"""

val kotlinLicenceHeader = """/*
$licenceText
*/

"""

val ktlintVersion = "0.49.1"

spotless {
    format("markdown") {
        target("**/*.md")
        targetExclude(".gradle/**", ".gradle-cache/**", "build/**")
        indentWithSpaces()
        endWithNewline()
    }

    format("misc") {
        target("**/.gitignore", "**/*.yaml", "**/*.yml", "**/*.sh", "**/Dockerfile")
        targetExclude("**/*.md", ".gradle/**", ".gradle-cache/**", "build/**")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts", "gradle/*.gradle.kts", "buildSrc/*.gradle.kts")
        ktlint(ktlintVersion)

        licenseHeader(
            kotlinLicenceHeader,
            "@file|import|tasks|apply|plugins|rootProject|dependencyResolutionManagement"
        )

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlin {
        target("src/**/*.kt", "buildSrc/**/*.kt")
        targetExclude("buildSrc/build/**")
        ktlint(ktlintVersion)

        licenseHeader(kotlinLicenceHeader)

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        target("src/**/*.java")

        licenseHeader(kotlinLicenceHeader)

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks.named("spotlessKotlinCheck") {
    mustRunAfter(tasks.withType<Test>().names)
}
