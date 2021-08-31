<img src="docs/klogging.svg" width="20%" height="auto" alt="Klogging Library"
align="right"/>

# Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/klogging-jvm.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22io.klogging%22%20AND%20a:%22klogging-jvm%22)

**Klogging** is a pure-Kotlin logging library that aims to be flexible and
easy to use. It uses Kotlin idioms for creating loggers and sending log
events. It takes advantage of
[Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html) in
environments that use them, for example the [Ktor](https://ktor.io)
asynchronous service framework.

See [https://klogging.io](https://klogging.io) for more detailed documentation.

🚧 **Klogging is alpha software in rapid development. The API and key
implementation details will change** 🚧

## Contents

- [Goals](#goals)
- [Quick start (JVM)](#quick-start-jvm)
- [Why another logging library?](#why-another-logging-library)
  - [Why not Logback or Log4j?](#why-not-logback-or-log4j)
  - [Why not KotlinLogging, Log4j Kotlin, etc.?](#why-not-kotlinlogging-log4j-kotlin-etc)

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

1. Include Klogging in your project with Gradle:

   ```kotlin
   implementation("io.klogging:klogging-jvm:0.3.1")
   ```

   or Maven:

   ```xml
   <dependency>
     <groupId>io.klogging</groupId>
     <artifactId>klogging-jvm</artifactId>
     <version>0.3.1</version>
   </dependency>
   ```

2. Configure logging early in your program startup using the configuration
   DSL. For simple logging to the console at INFO or higher level (more
   severe):

    ```kotlin
    fun main() = runBlocking {
        loggingConfiguration { defaultConsole() }
        // ...
    }
    ```

3. Create a `logger` attribute for a class, for example by using the `Klogging` interface for logging inside
   coroutines:

    ```kotlin
    class ImportantStuff : Klogging {
        suspend fun cleverAction(runId: String, input: String) = coroutineScope {
            launch(logContext("runId" to runId)) {
                logger.info("cleverAction using {input}", input)
            }
        }
    }
    ```
   
   Or by using the `NoCoLogging` interface for logging outside coroutines:

    ```kotlin
    class OtherStuff : NoCologging {
        fun funkyAction(input: String) {
            logger.info("funkyAction using {input}", input)
        }
    }
    ```

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

* They are designed to log strings of text. Whe you want to search for or filter logs by values
  within those messages you need to search within, or parse the strings.

* There are add-ons for including structured data in logs, for example
  [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder), but they feel
  clumsy to use.

* **MDC** (SLF4J/Logback) and **ThreadContext** (Log4j2) provide storage for context information but
  scopes are independent of thread lifecycles and need to be managed separately.

* Logback is hamstrung by having timestamp resolution limited to milliseconds. This limit is baked
  in to the
  [core of the library](https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83):
  that `long` value is milliseconds since the Unix Epoch.

### Why not KotlinLogging, Log4j Kotlin, etc.?

These libraries (mostly) wrap underlying Java libraries and suffer from the same limitations.
