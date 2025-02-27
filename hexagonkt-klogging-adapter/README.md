# Hexagon logging adapter for Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build-hexagonkt-adapter.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build-hexagonkt-adapter.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/hexagonkt-klogging-adapter.svg?label=maven%20central)](https://central.sonatype.com/search?smo=true&q=io.klogging%3Ahexagonkt-klogging-adapter)

A logging adapter for the [Hexagon](https://hexagonkt.com/) microservices toolkit.

## Quick start

Specify this library as the dependency. Gradle:

```kotlin
    implementation("io.klogging:hexagonkt-klogging-adapter:0.9.3")
```

Maven:

```xml

<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>hexagonkt-klogging-adapter</artifactId>
        <version>0.9.3</version>
    </dependency>
</dependencies>
```

Set the Klogging adapter at the start of your application, for example:

```kotlin
fun main() {
    LoggingManager.adapter = KloggingAdapter()
    server.start()
}
```

Configure Klogging either using the [configuration DSL](https://klogging.io/docs/configuration/dsl) or
a [configuration file](https://klogging.io/docs/configuration/json) in the application classpath. Here is a
simple `klogging.json` example:

```json
{
  "sinks": {
    "console": {
      "renderWith": "RENDER_ANSI",
      "sendTo": "STDOUT"
    }
  },
  "logging": [
    {
      "levelRanges": [
        {
          "fromMinLevel": "INFO",
          "toSinks": ["console"]
        }
      ]
    }
  ]
}
```

The Klogging adapter also handles all logging from the Hexagon core classes as well (via the Klogging SLFJ provider).
