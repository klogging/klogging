package io.klogging.events

import java.net.InetAddress

public actual val hostname: String = InetAddress.getLocalHost().hostName
