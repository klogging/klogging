package ktlogging.gelf

import ktlogging.events.LogEvent

actual fun LogEvent.toGelf(): String {
    TODO("Not yet implemented")
}

actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {}
