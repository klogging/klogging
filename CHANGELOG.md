# Klogging changes

## Version 0.4.0

- New architecture with multiple coroutine channels for handling log events.
- Batching events to send to sink destinations.
- Configure with `sendTo` instead of `dispatchTo`.
- Sink for sending log events to Splunk servers.
- More comprehensive diagnostics using internal logger.
- Coloured, column-aligned output in console renderer.

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
