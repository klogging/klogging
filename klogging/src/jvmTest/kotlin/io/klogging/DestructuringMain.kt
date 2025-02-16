package io.klogging

import io.klogging.config.loggingConfiguration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


data class User(val name: String, val age: Int)
enum class Source { MOBILE, WEB }
data class Login(val user: User, val source: Source)

suspend fun main() = coroutineScope {

    val logger = logger("io.klogging.DestructuringMain")

    loggingConfiguration(append = true) {
        kloggingMinLogLevel = Level.DEBUG
        minDirectLogLevel = Level.DEBUG
    }
    val user = User("John", 23)
    val login = Login(user, Source.MOBILE)

    logger.info("Login: {@login}", login)

    delay(500)
}