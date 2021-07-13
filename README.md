# KtLogging

**KtLogging** is a logging library for Kotlin applications that use coroutines, such as
the [Ktor](https://ktor.io) asynchronous service framework.

## Intention

KtLogging is designed to be used by applications running in complex, distributed environments. This
means emitting log events that can contain:

* A timestamp to microsecond or better resolution.
* Indication of the level or severity of the situation.
* Details of any exception that has occurred.
* A message about the situation at that point in the code.
* Local values of named objects, which may have complex structure.
* Context information, nested as deeply as required.

### Nested context

In a log event like this imaginary one:

```json
{
  "timestamp": "2021-07-13T10:08:42.749342Z",
  "host": "7d8fb252",
  "app.name": "wobbegong",
  "app.version": "2021.07.423",
  "app.code.version": "8dc98ed97058cf3432f4e40bf90ead8ed0cfe1a7",
  "log.level": "WARN",
  "log.name": "xz.wg.svc.GrongeService",
  "correlationId": "2841a0c3b9d83574",
  "requestId": "ca5adef",
  "thread": "service-worker-15",
  "message": "Smadged widgee rahlbong with clozbin gugglesplat",
  "widgee": {
    "id": "rahlbong",
    "clozgin": "gogglesplat"
  }
}
```

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
  the [core of the library](https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/ILoggingEvent.java#L83)
  .

## Goals

* Simple logging of text messages.

* Conformance to standard logging levels and logger practices.

* Structured logging by default.

* Templated logging, like [Serilog](https://serilog.net/) in .NET.

* Inclusion of selected
  [coroutine context](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/coroutine-context.html)
  information in log events.

* Ability to add information to the coroutine context for logging purposes.

* All logging is asynchronous using coroutines.

* Output to different channels in different formats, including
  [GELF](https://www.graylog.org/features/gelf) and
  [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format).

* Kotlin multiplatform, so it is not confined to JVM usage.

## Why a new 