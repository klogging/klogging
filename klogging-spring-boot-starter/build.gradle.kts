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
    `java-library`
    id("com.vanniktech.maven.publish")
    id("klogging-signing")
}

group = "io.klogging"
description = "Starter for using Klogging for logging. An alternative to spring-boot-starter-logging"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    api(project(":klogging"))
    api(project(":slf4j-klogging"))
}

mavenPublishing {

    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    pom {
        name.set("klogging-spring-boot-starter")
        description.set("Spring Boot starter for Klogging logging library")
        url.set("https://klogging.io/")
        inceptionYear.set("2021")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("mjstrasser")
                name.set("Michael Strasser")
                email.set("mjstrasser@klogging.io")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/klogging/klogging.git")
            url.set("https://github.com/klogging/klogging")
        }
    }
}
