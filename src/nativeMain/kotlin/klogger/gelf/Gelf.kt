package klogger.gelf

import klogger.events.LogEvent

actual fun gelf(logEvent: LogEvent): String {
    TODO("Not yet implemented")
}

actual fun sendGelf(gelfEvent: String, endpoint: Endpoint) {}
