package io.klogging

import io.klogging.config.loggingConfiguration
import io.klogging.rendering.RENDER_ECS_DOTNET
import io.klogging.sending.STDOUT
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


data class User(val name: String, val age: Int)
enum class Source { MOBILE, WEB }
data class Login(val user: User, val source: Source)

suspend fun main() = coroutineScope {

    val logger = logger("io.klogging.DestructuringMain")

    loggingConfiguration(append = false) {
        kloggingMinLogLevel = Level.DEBUG
        minDirectLogLevel = Level.DEBUG
        sink("console", RENDER_ECS_DOTNET, STDOUT)
        logging { fromMinLevel(Level.DEBUG) { toSink("console") } }
    }
    val user = User("John", 23)
    val login = Login(user, Source.MOBILE)

    logger.info("Login: {@login}", login)

    delay(500)
}