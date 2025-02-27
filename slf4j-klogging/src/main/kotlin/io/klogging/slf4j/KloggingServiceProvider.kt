/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.slf4j

import io.klogging.context.Context
import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.MDC
import org.slf4j.helpers.BasicMDCAdapter
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

public const val REQUESTED_API_VERSION: String = "2.0.99"

/**
 * SLF4J service provider implementation for Klogging.
 */
public class KloggingServiceProvider : SLF4JServiceProvider {
    private lateinit var loggerFactory: ILoggerFactory
    private lateinit var markerFactory: IMarkerFactory
    private val mdcAdapter: MDCAdapter = BasicMDCAdapter()

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getMarkerFactory(): IMarkerFactory = markerFactory

    override fun getMDCAdapter(): MDCAdapter = mdcAdapter

    override fun getRequestedApiVersion(): String = REQUESTED_API_VERSION

    override fun initialize() {
        loggerFactory = NoCoLoggerFactory()
        markerFactory = BasicMarkerFactory()

        // Ensure any MDC items are included in every log event, whether an `NoCoLoggerWrapper` or not.
        Context.addItemExtractor {
            MDC.getCopyOfContextMap() ?: mapOf()
        }
    }
}
