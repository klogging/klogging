import klogger.Timestamp
import java.time.Instant

fun timestampNow() = Instant.now().let { Timestamp(it.epochSecond, it.nano.toLong()) }