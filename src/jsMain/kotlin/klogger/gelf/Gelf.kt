package klogger.gelf

import klogger.events.LogEvent

actual fun LogEvent.toGelf(): String {
    TODO("Not yet implemented")
}

actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {}
