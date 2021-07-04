package klogger.events

import java.net.InetAddress
import java.util.UUID

actual fun newId(): String = UUID.randomUUID().toString()

actual fun hostname(): String = InetAddress.getLocalHost().hostName
