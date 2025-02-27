<img src="docs/klogging.svg" width="20%" height="auto" alt="Klogging Library"
align="right"/>

# Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build-klogging.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build-klogging.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/klogging-jvm.svg?label=maven%20central)](https://central.sonatype.com/search?smo=true&q=io.klogging%3Aklogging-jvm)

**Klogging** is a pure-Kotlin logging library that aims to be flexible and
easy to use. It uses Kotlin idioms for creating loggers and sending log
events. It takes advantage of
[Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html) in
environments that use them, for example the [Ktor](https://ktor.io)
asynchronous service framework.

This repository also includes an [SLF4J provider](slf4j-klogging), a
[Spring Boot starter](klogging-spring-boot-starter) and a
[Hexagon logging adapter](hexagonkt-klogging-adapter)
that use Klogging.

See [https://klogging.io](https://klogging.io) for more detailed documentation.

## Contents

- [Goals](#goals)
- [Quick start (JVM)](#quick-start-jvm)
    - [I didn’t see any logs!](#i-didnt-see-any-logs)
    - [Using snapshot builds](#using-snapshot-builds)
- [Building Klogging](#building-klogging)
- [Why another logging library?](#why-another-logging-library)
    - [Why not Logback or Log4j?](#why-not-logback-or-log4j)

## Goals

- Provide a familiar logging experience for Java and C# developers.
- Create structured log events by default.
- Use [message templates](https://messagetemplates.org) for simple logging of
  both text and data.
- Use Kotlin coroutines for carrying scope context information to include in log
  events and for asynchronous dispatching of events.
- Finest possible resolution of timestamps, down to nanosecond if available.
- (Future) Pure Kotlin multiplatform. _Current development focuses on the
  JVM._

## Quick start (JVM)

Klogging supports JVM versions 8 and above, and Kotlin versions 1.6 and above.

1. Include Klogging in your project with Gradle:

   ```kotlin
   implementation("io.klogging:klogging-jvm:0.9.3")
   ```

   or Maven:

   ```xml
   <dependency>
     <groupId>io.klogging</groupId>
     <artifactId>klogging-jvm</artifactId>
     <version>0.9.3</version>
   </dependency>
   ```

2. Configure logging early in your program startup using the configuration
   DSL. For simple logging to the console at INFO or higher level (more
   severe):

    ```kotlin
    fun main() = runBlocking {
        loggingConfiguration { ANSI_CONSOLE() }
        // ...
    }
    ```

3. Create a `logger` attribute for a class, for example by using the `Klogging` interface for
   logging inside
   coroutines:

    ```kotlin
    class ImportantStuff : Klogging {
        suspend fun cleverAction(runId: String, input: String) = coroutineScope {
            launch(logContext("runId" to runId)) {
                logger.info { "cleverAction using $input" }
            }
        }
    }
    ```

   Or by using the `NoCoLogging` interface for logging outside coroutines:

    ```kotlin
    class OtherStuff : NoCologging {
        fun funkyAction(input: String) {
            logger.info { "funkyAction using $input" }
        }
    }
    ```
   These examples both call the `logger.info` function with a lambda whose value is only evaluated
   if `logger` is currently configured to log at `INFO` level or higher.

### I didn’t see any logs!

If you try out Klogging in a simple command-line program you might not see all the log messages you
expect to see. This example will not show the log message on the console:

```kotlin
suspend fun main() = coroutineScope {
    loggingConfiguration { ANSI_CONSOLE() }
    val logger = logger("main")
    logger.info("Hello, world!")
}
```

Klogging works asynchronously and the program completes before log events can be
sent. In this case you can add a coroutine delay or thread sleep before the program completes,
for example:

```kotlin
suspend fun main() = coroutineScope {
    loggingConfiguration { ANSI_CONSOLE() }
    val logger = logger("main")
    logger.info("Hello, world!")
    delay(50)
}
```

Or you can specify that log events with severity above a certain level are sent directly instead of
via coroutine channels:

```kotlin
suspend fun main() = coroutineScope {
    loggingConfiguration {
        ANSI_CONSOLE()
        minDirectLogLevel(Level.INFO)
    }
    val logger = logger("main")
    logger.info("Hello, world!")
}
```

See [Direct logging](https://klogging.io/docs/concepts/direct-logging) for more information.

> Klogging is designed primarily for long-running services and applications.
>
> I don’t know a reliable way to trap application shutdown and ensure all logs are sent before
> shutdown proceeds. [Let me know if you do](mailto:info@klogging.io).

### Using snapshot builds

If you want to use the latest snapshot builds, specify these in your `build.gradle.kts`:

```kotlin
repositories {
    // ...
    maven ("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // ...
    implementation("io.klogging:klogging-jvm:0.9.3-SNAPSHOT")
}
```

## Building Klogging

Clone this repository and run `./gradlew clean build`

## Why another logging library?

Klogging is designed from the ground up to be standalone, pure Kotlin and to
be used with coroutines.

I could not find a logging library for Kotlin that meets these requirements:

* Send structured log events by default.
* Simple, reliable capture and logging of information from the current execution scope.
* High-resolution timestamps to ensure log events are aggregated in the
  correct order.

### Why not Logback or Log4j?

These solid, but venerable Java libraries have formed the backbone of Java logging for more than 10
years. The limitations I find are:

* They are designed to log strings of text. When you want to search for or filter logs by values
  within those messages you need to search within, or parse the strings.

* There are add-ons for including structured data in logs, for example
  [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder), but they feel
  clumsy to use.

* **MDC** (SLF4J/Logback) and **ThreadContext** (Log4j2) provide storage for context information but
  scopes are independent of thread lifecycles and need to be managed separately.

* Logback is hamstrung by having timestamp resolution limited to milliseconds. This limit is baked
  in to the
  [core of the library](https://github.com/qos-ch/logback/blob/a154cd1b564d436c90a26b8cb1a2e8ffff0a4a47/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83):
  that `long` value is milliseconds since the Unix Epoch.
