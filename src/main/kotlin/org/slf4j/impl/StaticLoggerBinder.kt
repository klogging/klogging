package org.slf4j.impl

import io.klogging.slf4j.NoCoLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * Implementation of [org.slf4j.impl.StaticLoggerBinder] to provide the [ILoggerFactory]
 * instance.
 *
 * I believe this class must be in the `org.slf4j.impl` package for versions of SLF4J
 * before 1.8.
 */
class StaticLoggerBinder private constructor(
    private val loggerFactory: ILoggerFactory
) : LoggerFactoryBinder {

    companion object {
        /** Version of SLF4J used to create this binding. */
        const val REQUEST_API_VERSION = "1.7.31"

        /** Instance of this class available to Java via `StaticLoggingBinder.getSingleton()`. */
        @JvmStatic
        val singleton = StaticLoggerBinder(NoCoLoggerFactory())
    }

    private val loggerFactoryClassString = NoCoLoggerFactory::class.java.name

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getLoggerFactoryClassStr(): String = loggerFactoryClassString
}