# Klogging concepts

An overview of the concepts in the library.

## Log event

A **log event** (modelled by [`LogEvent`](../library/src/commonMain/kotlin/io/klogging/events/LogEvent.kt))
contains information at a point in time and the state of a running system.

Important properties of an event are:

| Property    | Description                                                                            |
|-------------|----------------------------------------------------------------------------------------|
| Timestamp   | The point in time when the event occurred, with microsecond or finer resolution.       |
| Logger      | Name of the logger that sent the event (e.g. a fully-qualified class name).            |
| Level       | An indication of the severity of the event.                                            |
| Host        | Name of the host where the event originated: important in distributed systems.         |
| Message     | A string message summarising what happened, that might be constructed from a template. |
| Stack trace | Details about an exception or error, if one is associate with the event.               |
| Items       | A map of useful information current at the time of the event.                          |

## Sending and dispatching events

A **Logger** sends events to the logging system that dispatches them to zero or
more **targets**.

## Log context

All log events should include information from all contexts, from its
immediate scope up to all enclosing scopes.

The [`LogContext`](../library/src/commonMain/kotlin/io/klogging/context/LogContext.kt)
class can be placed in a coroutine context for inclusion in log events.
See [Coroutines](Coroutines.md) for an example.
