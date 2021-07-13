package ktlogging.gelf

import ktlogging.events.Level
import ktlogging.events.Timestamp

/**
 * Map [Level]s to syslog levels used by Graylog:
 *
 * 0=Emergency,1=Alert,2=Critical,3=Error,4=Warning,5=Notice,6=Informational,7=Debug
 */
fun graylogLevel(level: Level) = when (level) {
    Level.TRACE -> 7
    Level.DEBUG -> 7
    Level.INFO -> 6
    Level.WARN -> 4
    Level.ERROR -> 3
    Level.FATAL -> 2
}

data class Endpoint(
    val host: String = "localhost",
    val port: Int = 12201,
)

fun Timestamp.graylogFormat(): String {
    val ns = "000000000$nanos"
    return "$epochSeconds.${ns.substring(ns.length - 9)}"
}
