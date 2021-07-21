package ktlogging.clef

import ktlogging.events.LogEvent

/**
 * Serialises a [LogEvent] into [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format)
 * compact JSON format.
 */
expect fun LogEvent.toClef(): String

/**
 * Posts a CLEF-serialised event to the specified server using HTTP.
 */
expect fun dispatchClef(clefEvent: String, server: String = "http://localhost:5341")
