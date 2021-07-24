package ktlogging.gelf

import ktlogging.events.LogEvent

public expect fun LogEvent.toGelf(): String

public expect fun dispatchGelf(gelfEvent: String, endpoint: Endpoint = Endpoint())
