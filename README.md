# KtLogging

**KtLogging** is a logging library for Kotlin applications that use coroutines, such as
the [Ktor](https://ktor.io) asynchronous service framework.

> This is work in progress

## Intention

- Familar logging idioms for Java and C# devs.
- Kotlin coroutines for carrying contextual information to include in log events.
- Pure Kotlin multiplatform (future).
- Structured logs by default: not string messages with structured information a clumsy add-on.
- Message templates for simple logging of both text and data.
- Timestamp precision down to nanosecond if available.

## Why another logging library?

KtLogging is designed from the ground up to be standalone, pure Kotlin and to be used with
coroutines. No other library I could find meets these requirements.

### Why not … ?

#### Logback / Log4j

These venerable Java libraries have formed the backbone of Java logging for more than 10 years. The
limitations I find are:

* They are designed to log strings of text with embedded information that is discovered by searching
  within strings. Logging needs to include …

* Logback is hamstrung by having timestamp resolution limited to milliseconds. This limit is baked
  in to
  the [core of the library](https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83).
  
