package ktlogging.gelf

import ktlogging.events.LogEvent

public actual fun LogEvent.toGelf(): String {
    TODO("Not yet implemented")
}

public actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {}
