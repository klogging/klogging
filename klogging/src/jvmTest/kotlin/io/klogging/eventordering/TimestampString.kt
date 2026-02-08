package io.klogging.eventordering

import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import io.klogging.rendering.evalTemplate

class TimestampString : RenderString {
    override fun invoke(event: LogEvent): String =
        "${event.timestamp}\t${event.id}\t${event.context}\t${event.evalTemplate()}"
}
