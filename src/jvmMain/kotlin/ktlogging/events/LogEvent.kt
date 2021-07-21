package ktlogging.events

import java.net.InetAddress

actual fun hostname(): String = InetAddress.getLocalHost().hostName
