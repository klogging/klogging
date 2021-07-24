package ktlogging.events

/**
 * Name of the executing host, included in all log events.
 */
public expect val hostname: String

/**
 * An event at a point in time with information about the running state of
 * a program.
 */
public data class LogEvent(
    /** When the event occurred, to microsecond or better precision. */
    val timestamp: Timestamp,
    /** Host where the event occurred. */
    val host: String = hostname,
    /** Name of the logger that emitted the event. */
    val logger: String,
    /** Severity [Level] of the event. */
    val level: Level,
    /** [Message template](https://messagetemplates.org), if any, used to construct the message. */
    val template: String? = null,
    /** Message describing the event. */
    val message: String,
    /** String stack trace information that may be included if an exception is associated with the event. */
    val stackTrace: String?,
    /**
     * Map of items current at the time of the event, to be displayed as structured data.
     *
     * If the message string was constructed from a template, there is one item per
     * hole in the template.
     */
    val items: Map<String, Any?>,
)
