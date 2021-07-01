package klogger

import java.util.UUID

actual fun newId(): String = UUID.randomUUID().toString()