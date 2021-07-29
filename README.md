<img src="docs/klogging.svg" width="60px" height="60px" alt="Klogging logo"/>

# Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/klogging-jvm.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22io.klogging%22%20AND%20a:%22klogging-jvm%22)

**Klogging** is a pure-Kotlin logging library that aims to be flexible and easy to use.
It uses Kotlin idioms for creating loggers and sending log events.
It takes advantage of [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html)
in environments that use them, for example the [Ktor](https://ktor.io) asynchronous service
framework.

## Intention

🚧 **This is work in progress** 🚧

- Familiar logging idioms for Java and C# devs.
- Kotlin coroutines for carrying contextual information to include in log events.
- Structured logs by default: not string messages with structured information a clumsy add-on.
- [Message templates](https://messagetemplates.org) for elegant logging of both text and data.
- Timestamp precision down to nanosecond if available
- Configuration of logging levels by logger names to targets (like Log4j and Logback).
- Pure Kotlin multiplatform (future). _Current development focuses on the JVM._

## Quick start (JVM only)

1. Include Klogging in your project with Gradle:

   ```kotlin
   implementation("io.klogging:klogging-jvm:0.1.0")
   ```

   or Maven:

   ```xml
   <dependency>
     <groupId>io.klogging</groupId>
     <artifactId>klogging-jvm</artifactId>
     <version>0.1.0</version>
   </dependency>
   ```

2. (Optionally) configure loggers using `LoggingConfiguration.setConfigs()`.

3. Create a logger, for example by using the `Klogging` interface for coroutine logging:

    ```kotlin
    class ImportantStuff : Klogging {
        suspend fun cleverAction(runId: String, input: String) = coroutineScope {
            launch(logContext("runId" to runId)) {
                logger.info("cleverAction with input=$input")
            }
        }
    }
    ```

## Use cases

This section will cover:

- Creating loggers
- Logging events
- Adding information to the coroutine context to be logged
- Configuring logging levels, dispatchers and targets

## Why another logging library?

Klogging is designed from the ground up to be standalone, pure Kotlin and to be used with
coroutines. It is designed to be used by distributed services that log events with information
from a wide variety of contexts.

No other library I could find meets these requirements.

### Why not … ?

#### Logback / Log4j

These venerable Java libraries have formed the backbone of Java logging for more than 10 years. The
limitations I find are:

* They are designed to log strings of text with embedded information that is discovered by searching
  within strings. Logging should be of events containing structured information derived from
  all nested scopes where those events occur.

* Logback is hamstrung by having timestamp resolution limited to milliseconds. This limit is baked
  in to
  the [core of the library](https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83).

**TBC**
