package klogger.gelf

import klogger.events.Level
import klogger.events.LogEvent

expect fun LogEvent.toGelf(): String

expect fun dispatchGelf(gelfEvent: String, endpoint: Endpoint = Endpoint())
