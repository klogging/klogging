# Klogging Roadmap

In no particular order at this stage.

## Goals

- Nanosecond precision in logging timestamps
- Ease of use in API for developers
- Close but not too close in API to popular logging APIs

## Within the library

- Support platforms other than JVM.
- Extensive performance testing of event creation and dispatching.
- More string rendering options, e.g. with colouring of logger level.
- More built-in dispatching and rendering options, e.g. Logstash, syslog.
- Configuration from YAML and/or JSON files.
- [Message template](https://messagetemplates.org) processing for sinks other
  than Seq. Consider library reuse rather than internal implementation.
- Consider service loader discovery or similar patterns for 3rd-party
  integrations such as SLF4J so users only pull in relevant dependencies.
- Consider turning `KloggingConfiguration` from a global singleton into a class and a default
  function to return a common, shared instance.

## Additions to the library

- Create a [Ktor](https://ktor.io/) plugin with options for automatically
  capturing and logging request and other context information.
- Create an [SLF4J](http://www.slf4j.org/) binding for Klogging. Consider the
  impact of SLF4J using only millisecond precision.
- Consider a [Log4j2](https://logging.apache.org/log4j/2.x/manual/api.html)
  binding for Klogging.
- Consider a JUL (JDK native logging) binding for Klogging.
- A repository of examples in Kotlin and Java
- A complete documentation and support website, using ideas from
  [The Documentation System](https://documentation.divio.com/).
