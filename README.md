# Klogger

**Klogger** (working title) is a logging library for Kotlin applications that
use coroutines, such as the [Ktor](https://ktor.io) asynchronous service framework.

## Goals

* Simple logging of text messages.

* Conformance to standard logging levels and logger practices.

* Structured logging by default.

* Templated logging, like [Serilog](https://serilog.net/) in .NET.

* Inclusion of selected
  [coroutine context](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/coroutine-context.html)
  information in log events.

* Ability to add information to the coroutine context for logging purposes.

* All log output is asynchronous using coroutines.

* Output to different channels in different formats, such as
  [GELF](https://www.graylog.org/features/gelf) and
  [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format).
