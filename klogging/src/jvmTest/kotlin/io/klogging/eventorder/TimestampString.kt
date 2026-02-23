package io.klogging.eventorder

import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import io.klogging.rendering.evalTemplate
import io.klogging.rendering.localTime

class TimestampString : RenderString {
    override fun invoke(event: LogEvent): String = "${event.timestamp.localTime}\t${event.id}\t${event.evalTemplate()}"
}
