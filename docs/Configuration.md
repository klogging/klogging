# Configuration

🚧 **Work in progress** 🚧

Goals for logging configuration are:

- Code first, using a simple domain-specific language.
- Configuration files may override configuration in code.
- Environment variables can be used to specify some values.

## Configuration DSL

Here is a complex example.

```kotlin
import com.example.logging.renderAudit

import io.klogging.config.loggingConfiguration
import io.klogging.dispatching.STDERR
import io.klogging.dispatching.STDOUT
import io.klogging.render.RENDER_CLEF
import io.klogging.render.RENDER_SIMPLE

loggingConfiguration {
    // Dispatch to standout output stream with simple message rendering.
    sink("stdout", STDOUT, RENDER_SIMPLE)
    // Dispatch to standout error stream with simple message rendering.
    sink("stderr", STDERR, RENDER_SIMPLE)
    // Dispatch to a Seq server with CLEF rendering by default.
    sink("seq", seq(server = "http://localhost:5341"))
    // Dispatch to a syslog endpoint, with custom rendering.
    sink("auditing") {
        syslog(config = "syslogConfig") { render(renderAudit) }
    }
    logging {
        // Log everything from `com.example` base.
        fromLoggerBase("com.example")
        // INFO level only.
        atLevel(Level.INFO) {
            // To both standard out and Seq.
            toSink("stdout")
            toSink("seq")
        }
        // WARN level and above (more severe).
        fromMinLevel(Level.WARN) {
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

## Configuration files

**TBC**

## Environment variables

**TBC**
