package klogger.clef

import klogger.events.LogEvent

expect fun clef(logEvent: LogEvent): String

expect fun sendClef(clefEvent: String, server: String = "http://localhost:5341")
