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

package io.klogging.build

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

const val licenceText = """
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
"""

const val kotlinLicenceHeader = """/*
$licenceText
*/

"""

fun Project.configureSpotless(ktlintVersion: String) {
    apply<SpotlessPlugin>()

    configure<SpotlessExtension> {
        format("markdown") {
            target(
                fileTree(
                    mapOf(
                        "dir" to ".",
                        "include" to listOf("**/*.md"),
                        "exclude" to listOf(".gradle/**", ".gradle-cache/**", ".batect/**", "build/**")
                    )
                )
            )

            indentWithSpaces()
            endWithNewline()
        }

        format("misc") {
            target(
                fileTree(
                    mapOf(
                        "dir" to ".",
                        "include" to listOf("**/.gitignore", "**/*.yaml", "**/*.yml", "**/*.sh", "**/Dockerfile"),
                        "exclude" to listOf("**/*.md", ".gradle/**", ".gradle-cache/**", ".batect/**", "build/**")
                    )
                )
            )

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

        kotlinGradle {
            target("*.gradle.kts", "gradle/*.gradle.kts", "buildSrc/*.gradle.kts")
            ktlint(ktlintVersion)

            @Suppress("INACCESSIBLE_TYPE")
            licenseHeader(kotlinLicenceHeader, "import|tasks|apply|plugins|rootProject")

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

        kotlin {
            target("src/**/*.kt", "buildSrc/**/*.kt")
            ktlint(ktlintVersion)

            @Suppress("INACCESSIBLE_TYPE")
            licenseHeader(kotlinLicenceHeader)

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
    }

    tasks.named("spotlessKotlinCheck") {
        mustRunAfter("jvmTest")
    }
}
