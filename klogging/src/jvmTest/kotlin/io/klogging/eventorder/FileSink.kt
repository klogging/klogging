package io.klogging.eventorder

import io.klogging.sending.SendString
import kotlinx.datetime.Clock
import java.io.File

class FileSink : SendString {
    val testPath = "./klogging/src/jvmTest/kotlin/io/klogging/eventorder"
    private val logFile = File("$testPath/${Clock.System.now().epochSeconds}.txt")

    override fun invoke(eventString: String) {
        logFile.appendText("$eventString\n")
    }
}
