package klogger.gelf

data class Endpoint(
    val host: String = "localhost",
    val port: Int = 12201
)

expect fun send(gelfEvent: String, endpoint: Endpoint = Endpoint())
