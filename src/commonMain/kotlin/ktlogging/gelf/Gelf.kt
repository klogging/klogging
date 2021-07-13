package ktlogging.gelf

import ktlogging.events.LogEvent

expect fun LogEvent.toGelf(): String

expect fun dispatchGelf(gelfEvent: String, endpoint: Endpoint = Endpoint())
