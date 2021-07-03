package klogger.gelf

data class Endpoint(
    val host: String = "localhost",
    val port: Int = 12201,
)

expect fun sendGelf(gelfEvent: String, endpoint: Endpoint = Endpoint())
