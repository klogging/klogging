package io.klogging.gelf

import io.klogging.events.LogEvent

public expect fun LogEvent.toGelf(): String

public expect fun dispatchGelf(gelfEvent: String, endpoint: Endpoint = Endpoint())
