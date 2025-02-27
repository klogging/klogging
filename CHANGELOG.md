# Klogging changes

## Version 0.9.3
- Support SLF4J 2.0.17 – [issue 340](https://github.com/klogging/klogging/issues/340) - Thanks @gavvvr!

## Version 0.9.2
- KotlinX Serialization 1.8.0
- Object destructuring used in all JSON-generating renderers [JVM] - [issue 328](https://github.com/klogging/klogging/issues/328)

## Version 0.9.1
- Non-strict version of Kotlin Serialization - [issue 337](https://github.com/klogging/klogging/issues/337)
- Gradle 8.12.1

## Version 0.9.0
- Object destructuring in message templates with `@` operator [JVM] - [issue 328](https://github.com/klogging/klogging/issues/328)
- Use non-blocking coroutine dispatcher for sending to sinks [JVM] - [issue 320](https://github.com/klogging/klogging/issues/320)
- Many dependency updates

## Version 0.8.0
- String pattern-based rendering definitions - [issue 306](https://github.com/klogging/klogging/issues/306)
- Dependency updates

## Version 0.7.3
- Gradle version update to 8.10.2
- Dependency updates
- KMP updates
- Improvements to Splunk and ELK sending

## Version 0.7.2
- `RenderString` and `SendString` are specified using `fun interface`.
- Kotlin version update to 2.0.20
- Gradle version update to 8.10.
- Other dependency updates.

## Version 0.7.1
- Extracted extensions for creating custom renderers - [issue 145](https://github.com/klogging/klogging/issues/145)

## Version 0.7.0
- From [Baris](https://github.com/peacecwz): RENDER_STANDARD: standard rendering of log events to JSON - [PR 282](https://github.com/klogging/klogging/pull/282)

## Version 0.6.1
- Replace loggers defined with the same name so their attached contexts change – [issue 274](https://github.com/klogging/klogging/issues/274)

## Version 0.6.0
- Klogger and NoCoLogger instances can have their own context items – [issue 274](https://github.com/klogging/klogging/issues/274)

## Version 0.5.14
- RENDER_ECS_DOTNET renderer

## Version 0.5.13
- JVM: load configuration files from current thread classpath

## Version 0.5.12

- Fix SLF4J provider issue with template and multiple arguments
- Add RENDER_ECS to named renderers map
- Multiple dependency updates

## Version 0.5.11

- Enable loading of `object` renderers and senders from the
  classpath – [issue 211](https://github.com/klogging/klogging/issues/211)
- Ensure file configuration is loaded before processing any DSL
  configuration – [issue 232](https://github.com/klogging/klogging/issues/232)

## Version 0.5.10

- Reimplemented functional typealiases `RenderString`, `SendString` and `EventSender` as interfaces to support loading
  named values from the classpath – [issue 211](https://github.com/klogging/klogging/issues/211)
- Improved JDK Platform Logging implementation – [issue 231](https://github.com/klogging/klogging/issues/231)
- Ability to set base context items in configuration
  files – [issue 148](https://github.com/klogging/klogging/issues/148)
- Fixed bug in loading and parsing HOCON configuration
  files – [issue 233](https://github.com/klogging/klogging/issues/233)

## Version 0.5.9

- Ability to explicitly include a map of items in a logging call –
  [discussion 221](https://github.com/klogging/klogging/discussions/221)
- Initial release of Klogging implementation of JDK Platform Logging –
  [issue 231](https://github.com/klogging/klogging/issues/231)
- Minor updates to dependencies

## Version 0.5.8

- Fix for bug in `RENDER_ANSI` when logger names have too many delimited
  parts – [issue 218](https://github.com/klogging/klogging/issues/218)
- Load custom rendering and sending classes specified by fully-qualified class names in JSON and HOCON
  configuration files – [issue 211](https://github.com/klogging/klogging/issues/211)
- Support double braces in message templates
- Kotlin 1.9.22
- Starting to apply coding standards using Diktat
- Build with Gradle 8.5

## Version 0.5.7

- Workaround for issue `NoClassDefFoundError` thrown when using Spring Boot Starter
  (see [issue 188](https://github.com/klogging/klogging/issues/188))
- Send Splunk-formatted messages to any sink (not just an HEC endpoint)
- Support Hexagon microservices toolkit 3.4.3, requiring JDK 17
- Use Kotlin 1.9.20
- Build with Gradle 8.4

## Version 0.5.6

- Include sources in SLF4J provider and Hexagon logging adapter

## Version 0.5.5

- Logging adapter for [Hexagon microservices toolkit](https://hexagonkt.com/)

## Version 0.5.4

- Use Gradle `api()` for `kotlinx-coroutines` so it is available transitively to consuming applications

## Version 0.5.3

- Seq server configuration accepts `apiKey` and works with HTTPS endpoints
- Fixed direct sending to not use any coroutines
- Specify config file path in DSL

## Version 0.5.2

- `ItemExtractor` functions that can add items to any log events
- [#149](https://github.com/klogging/klogging/issues/149) SLF4J provider uses `ItemExtractor` function to include MDC
  items in all log events, including those from non-SLF4J loggers

## Version 0.5.1

- Send internal logger INFO messages after configuration is set

## Version 0.5.0

A number of bug fixes and improvements.

- [Console renderer with colouring of level strings](https://klogging.io/docs/configuration/built-ins#render_ansi)
- [Base context items](https://klogging.io/docs/configuration/context-items#base-context)
- [Mapping from other coroutine contexts, e.g. Project Reactor](https://klogging.io/docs/configuration/context-items#from-other-coroutine-context-elements)
- [Configuration from HOCON files](https://klogging.io/docs/configuration/hocon)
- [Direct, synchronous logging as well as asynchronous via coroutines](https://klogging.io/docs/concepts/direct-logging)
- [Stop on match for logger names in configuration](https://klogging.io/docs/configuration/dsl#short-circuit-matching-with-stoponmatch)
- [One-line renderer with ISO8601 timestamps](https://klogging.io/docs/configuration/built-ins#render_iso8601)
- [Env var for setting built-in renderer for a sink](https://klogging.io/docs/internals/environment-variables)
- [`toMaxLevel` function for defining loggers](https://klogging.io/docs/configuration/dsl#fromminlevel-tomaxlevel-atlevel-and-inlevelrange)

## Version 0.4.0

- New architecture with multiple coroutine channels for handling log events.
- Batching events to send to sink destinations.
- Configure with `sendTo` instead of `dispatchTo`.
- Sink for sending log events to Splunk servers.
- More comprehensive diagnostics using internal logger.
- Coloured, column-aligned output in console renderer.
