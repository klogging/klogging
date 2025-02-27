# Spring Boot starter for Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build-spring-boot-starter.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build-spring-boot-starter.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/klogging-spring-boot-starter.svg?label=maven%20central)](https://central.sonatype.com/search?smo=true&q=io.klogging%3Aklogging-spring-boot-starter)

See [Klogging documentation](https://klogging.io/docs/java/spring-boot) for more details.

## Set up Gradle:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "ch.qos.logback")
    }
    implementation("io.klogging:klogging-spring-boot-starter:0.9.3")
    // Other runtime dependencies.
    
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "ch.qos.logback")
    }
    // Other test dependencies.
}
```

## Configure Klogging

Put a `klogging.json` file in the `src/main/resources` directory of the project.

Here is a simple one for logging to the console.

```json
{
  "sinks": {
    "stdout": {
      "renderWith": "RENDER_ANSI",
      "sendTo": "STDOUT"
    }
  },
  "logging": [
    {
      "levelRanges": [
        {
          "fromMinLevel": "INFO",
          "toSinks": [
            "stdout"
          ]
        }
      ]
    }
  ]
}
```
