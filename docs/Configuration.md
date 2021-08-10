# Configuration

🚧 **Work in progress** 🚧

Goals for logging configuration are:

- Code first, using a simple domain-specific language.
- Configuration files may override configuration in code.
- Environment variables can be used to specify some values.

## Configuration DSL

Here is a complex example.

```kotlin
import com.example.logging.render
import com.example.logging.renderAudit

import io.klogging.config.loggingConfiguration
import io.klogging.dispatching.STDERR
import io.klogging.dispatching.STDOUT
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_SIMPLE

loggingConfiguration {
    // Render as a string message and send to standout output stream.
    sink("stdout", RENDER_SIMPLE, STDOUT)
    // Render as a string message and send to standout error stream.
    sink("stderr", RENDER_SIMPLE, STDERR)
    // Render as CLEF (by default) and send to a Seq server.
    sink("seq", seq(server = "http://localhost:5341"))
    // Send to a syslog endpoint, with custom rendering.
    sink("auditing") {
        syslog(config = "syslogConfig") { render(renderAudit) }
    }
    logging {
        // Log everything from `com.example` base.
        fromLoggerBase("com.example")
        // INFO level only.
        atLevel(INFO) {
            // To both standard out and Seq.
            toSink("stdout")
            toSink("seq")
        }
        // WARN level and above (more severe).
        fromMinLevel(WARN) {
            // To both standard error and Seq.
            toSink("stderr")
            toSink("seq")
        }
    }
    logging {
        // Exact logger name (e.g. one class).
        exactLogger("com.example.service.FancyService")
        // Log from DEBUG to Seq.
        fromMinLevel(Level.DEBUG) { to Sink("seq") }
    }
    logging {
        // Log all audit events.
        fromLoggerBase("audit")
        // To the auditing sink.
        toSink("auditing")
    }
}
```

**NB:** Dispatching to syslog is not currently part of Klogging.

### `loggingConfiguration`

This function creates a configuration for the running program. It makes sense to call this as early as
possible in program startup. It configures using specifications in the supplied lambda.

By default, the configuration replaces any existing one:

```kotlin
loggingConfiguration {
    // ...
}
```

To append a new configuration to an existing one:

```kotlin
loggingConfiguration(append = true) {
    // ...
}
```

### `sink`

This function configures a named sink with a renderer and a dispatcher.

- A renderer _renders_ a [LogEvent](../src/commonMain/kotlin/io/klogging/events/LogEvent.kt)
into a string.

- A dispatcher _dispatches_ a string somewhere.

This example configures two sinks:

```kotlin
    sink("stdout", RENDER_SIMPLE, STDOUT)
    sink("seq", seq("http://localhost:5341"))
```

- The `stdout` sink renders events with the built-in renderer `RENDER_SIMPLE` and dispatches them
  to the standard output using the built-in `STDOUT` dispatcher.
- The `seq` sink uses the built-in `seq` function for rendering events in
  [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format) compact JSON format and
  dispatching them to a [Seq](https://datalust.co/seq) server running locally.

Sinks must be declared before they are referenced in `toSink` functions.

### `logging`

This function configures logging from specified loggers at specified levels to specified
sinks. The following sections explain details.

### `fromLoggerBase` and `exactLogger`

These functions specify how to match logger names. For example:

- `fromLoggerBase("com.example")` matches all loggers with names that start with `com.example`, such
  as `com.example.config.ConfigApp`, `com.example.services.BlodgeService` etc.
- `exactLogger("com.example.services.GlubService")` matches only the logger called
  `com.example.services.GlubService`. No other logger with match.

Using these functions enables the configuration of logger hierarchies, like in Log4J and Logback.

> **Note:** These functions are optional: if logger names are not specified, all loggers will match.
> This defines the equivalent of a root logger in Log4J or Logback.

### `fromMinlevel` and `atLevel`

These functions specify the levels at which to dispatch log events. For example:

- `fromMinLevel(Level.INFO)` will enable all events at `INFO` level and above (i.e. more severe:
  `WARN`, `ERROR` and `FATAL`) to be dispatched.
- `atLevel(Level.WARN)` enables only events at `WARN` level to be dispatched by matching loggers.

The functions accept a lambda to specify which sinks to dispatch to.

### `toSink`

This function specifies the name of a sink to dispatch logs to. It can be called mulitple times for
a level specification. The sink must have been defined previously by name, otherwise a short warning
is written to the console and the configuration is ignored.

An example:

```kotlin
    fromMinLevel(INFO) {
        toSink("console")
        toSink("seq")
    }
```

During dispatching, an event is never dispatched to a sink more than once. Given this configuration:

```kotlin
    logging {
        fromLoggerBase("com.example")
        fromMinLevel(INFO) {
            toSink("stdout")
            toSink("seq")
        }
        fromMinLevel(WARN) {
            toSink("stderr")
            toSink("seq")
        }
    }
```

An event from logger `com.example.nurdling.NurdleController` at level `WARN` is dispatched to `seq` only once.
There is no need to disable additivity as in Log4J and Logback.

## Configuration files

**TBC**

## Environment variables

**TBC**
