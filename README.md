# KtLogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build and test](https://github.com/mjstrasser/ktlogging/actions/workflows/build-test.yml/badge.svg)](https://github.com/mjstrasser/ktlogging/actions/workflows/build-test.yml)

**KtLogging** is a pure-Kotlin logging library that aims to be flexible and easy to use.
It uses Kotlin idioms for creating loggers and sending log events.
It takes advantage of [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html)  
in environments that use them, for example the [Ktor](https://ktor.io) asynchronous service
framework.

## Intention

**This is work in progress**

- Familiar logging idioms for Java and C# devs.
- Kotlin coroutines for carrying contextual information to include in log events.
- Structured logs by default: not string messages with structured information a clumsy add-on.
- [Message templates](https://messagetemplates.org) for elegant logging of both text and data.
- Timestamp precision down to nanosecond if available
- Configuration of logging levels by logger names to targets (like Log4j and Logback). 
- Pure Kotlin multiplatform (future). _Current development focuses on the JVM._

## How to use it

_Coming soon_

## Use cases

This section will cover:

- Creating loggers
- Logging events
- Adding information to the coroutine context to be logged
- Configuring logging levels, dispatchers and targets

## Why another logging library?

KtLogging is designed from the ground up to be standalone, pure Kotlin and to be used with
coroutines. It is designed to be used by distributed services that log events with information
from a wide variety of contexts.

No other library I could find meets these requirements.

### Why not … ?

#### Logback / Log4j

These venerable Java libraries have formed the backbone of Java logging for more than 10 years. The
limitations I find are:

* They are designed to log strings of text with embedded information that is discovered by searching
  within strings. Logging needs to include …

* Logback is hamstrung by having timestamp resolution limited to milliseconds. This limit is baked
  in to
  the [core of the library](https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83).
  
