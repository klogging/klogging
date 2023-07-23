# Spring Boot starter for Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging-spring-boot-starter/actions/workflows/build.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/klogging-spring-boot-starter.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22io.klogging%22%20AND%20a:%klogging-spring-boot-starter%22)

See [Klogging documentation](https://klogging.io/docs/java/spring-boot) for more details.

## Set up Gradle:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "ch.qos.logback")
    }
    implementation("io.klogging:klogging-spring-boot-starter:0.3.5")
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
