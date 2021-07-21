package ktlogging.events

import java.net.InetAddress

actual val hostname: String = InetAddress.getLocalHost().hostName
