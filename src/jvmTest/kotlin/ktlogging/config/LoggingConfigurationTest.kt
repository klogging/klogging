package ktlogging.config

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import ktlogging.config.LoggingConfiguration.dispatchersFor
import ktlogging.dispatching.DispatchEvent
import ktlogging.events.Level
import ktlogging.randomString

class LoggingConfigurationTest : DescribeSpec({

    describe("dispatchersFor() function") {
        val dispatchEvent: DispatchEvent = { e -> println(e) }

        describe("for the root logger") {
            it("returns dispatchers from loggers with higher level than the event level") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                LoggingConfiguration.setConfigs(LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(rootDispatcher)))

                dispatchersFor(randomString(), Level.WARN) shouldContain rootDispatcher
            }
            it("returns dispatchers from loggers with the same level as the event level") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                LoggingConfiguration.setConfigs(LoggingConfig(ROOT_CONFIG, Level.WARN, listOf(rootDispatcher)))

                dispatchersFor(randomString(), Level.WARN) shouldContain rootDispatcher
            }
            it("does not return dispatchers from loggers with lower level than the event level") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                LoggingConfiguration.setConfigs(LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(rootDispatcher)))

                dispatchersFor(randomString(), Level.DEBUG) shouldNotContain rootDispatcher
            }
        }

        describe("when the event level is at or higher than that of the logger") {
            it("returns all dispatchers from the root logger") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                val otherDispatcher = LogDispatcher(randomString(), dispatchEvent)
                LoggingConfiguration.setConfigs(
                    LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(rootDispatcher)),
                    LoggingConfig(randomString(), Level.TRACE, listOf(otherDispatcher))
                )

                dispatchersFor(randomString(), Level.INFO) shouldContain rootDispatcher
            }
            it("returns dispatchers from loggers with names that match the start of the event logger") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                val otherDispatcher = LogDispatcher(randomString(), dispatchEvent)
                val configName = randomString()
                LoggingConfiguration.setConfigs(
                    LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(rootDispatcher)),
                    LoggingConfig(configName, Level.INFO, listOf(otherDispatcher)),
                )

                dispatchersFor("$configName${randomString()}", Level.INFO) shouldContain otherDispatcher
            }
            it("does not return dispatchers from loggers with names that do not match the start of the event logger") {
                val rootDispatcher = LogDispatcher(randomString(), dispatchEvent)
                val otherDispatcher = LogDispatcher(randomString(), dispatchEvent)
                val configName = randomString()
                LoggingConfiguration.setConfigs(
                    LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(rootDispatcher)),
                    LoggingConfig(configName, Level.INFO, listOf(otherDispatcher)),
                )

                dispatchersFor(randomString(), Level.INFO) shouldNotContain otherDispatcher
            }
        }
    }

})
