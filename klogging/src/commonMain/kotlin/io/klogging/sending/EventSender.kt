package io.klogging.sending

import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString

/** Interface for sending log events somewhere. */
public fun interface EventSender {
    /**
     * Send a batch of log events somewhere.
     *
     * @param batch list of events to send.
     */
    public operator fun invoke(batch: List<LogEvent>)
}

/**
 * Convert a [RenderString] and [SendString] into an [EventSender].
 *
 * @param renderer the [RenderString] that renders a log event into a string
 * @param sender the [SendString] that sends the rendered event string somewhere
 */
public fun senderFrom(renderer: RenderString, sender: SendString): EventSender =
    EventSender { batch -> sender(batch.joinToString("\n") { renderer(it) }) }
