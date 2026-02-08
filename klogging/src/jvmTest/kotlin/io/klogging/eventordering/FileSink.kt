package io.klogging.eventordering

import io.klogging.sending.SendString
import kotlinx.datetime.Clock
import java.io.File

class FileSink : SendString {
    private val logFile = File("./${Clock.System.now().epochSeconds}.txt")

    override fun invoke(eventString: String) {
        logFile.appendText("$eventString\n")
    }
}
