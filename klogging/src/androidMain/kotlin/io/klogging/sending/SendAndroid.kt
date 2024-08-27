package io.klogging.sending

import android.util.Log
import io.klogging.Level
import io.klogging.events.LogEvent
import io.klogging.rendering.evalTemplate
import io.klogging.rendering.itemsAndStackTrace

public val ANDROID: EventSender = object : EventSender {
    override fun invoke(batch: List<LogEvent>) {
        batch.forEach { event ->
            val level = when(event.level) {
                Level.TRACE -> Log.VERBOSE
                Level.DEBUG -> Log.DEBUG
                Level.INFO -> Log.INFO
                Level.WARN -> Log.WARN
                Level.ERROR -> Log.ERROR
                Level.FATAL -> Log.ASSERT
                Level.NONE -> Log.VERBOSE
            }

            val msg = buildString {
                append("[${event.context}] ${event.evalTemplate()}")
                append(event.itemsAndStackTrace)
            }

            Log.println(level, event.logger, msg)
        }
    }
}
