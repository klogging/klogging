package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import randomString

class TestLogger(
    private val level: Level = Level.FATAL
) : Klogger {

    internal var logged: String? = null

    override fun minLevel() = level

    override suspend fun logMessage(level: Level, message: String) {
        logged = message
    }

}

class KloggerTest : DescribeSpec({

    describe("Klogger") {
        describe("level check") {
            it("isTraceEnabled() is true only for TRACE") {
                TestLogger(Level.TRACE).isTraceEnabled() shouldBe true
                TestLogger(Level.DEBUG).isTraceEnabled() shouldBe false
                TestLogger(Level.INFO).isTraceEnabled() shouldBe false
                TestLogger(Level.WARN).isTraceEnabled() shouldBe false
                TestLogger(Level.ERROR).isTraceEnabled() shouldBe false
                TestLogger(Level.FATAL).isTraceEnabled() shouldBe false
            }
            it("isDebugEnabled() is true only for TRACE and DEBUG") {
                TestLogger(Level.TRACE).isDebugEnabled() shouldBe true
                TestLogger(Level.DEBUG).isDebugEnabled() shouldBe true
                TestLogger(Level.INFO).isDebugEnabled() shouldBe false
                TestLogger(Level.WARN).isDebugEnabled() shouldBe false
                TestLogger(Level.ERROR).isDebugEnabled() shouldBe false
                TestLogger(Level.FATAL).isDebugEnabled() shouldBe false
            }
            it("isInfoEnabled() is true only for TRACE, DEBUG and INFO") {
                TestLogger(Level.TRACE).isInfoEnabled() shouldBe true
                TestLogger(Level.DEBUG).isInfoEnabled() shouldBe true
                TestLogger(Level.INFO).isInfoEnabled() shouldBe true
                TestLogger(Level.WARN).isInfoEnabled() shouldBe false
                TestLogger(Level.ERROR).isInfoEnabled() shouldBe false
                TestLogger(Level.FATAL).isInfoEnabled() shouldBe false
            }
        }

        describe("logging at different levels:") {
            it("a TRACE logger logs at all levels") {
                val logger = TestLogger(Level.TRACE)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
            it("a DEBUG logger logs at all levels except TRACE") {
                val logger = TestLogger(Level.DEBUG)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
            it("an INFO logger logs at all levels except DEBUG and TRACE") {
                val logger = TestLogger(Level.INFO)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
            it("a WARN logger logs only at WARN, ERROR and FATAL") {
                val logger = TestLogger(Level.WARN)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
            it("an ERROR logger logs only at ERROR and FATAL") {
                val logger = TestLogger(Level.ERROR)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe it
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
            it("a FATAL logger logs only at FATAL") {
                val logger = TestLogger(Level.FATAL)
                randomString().let {
                    logger.trace(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.debug(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.info(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.warn(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.error(it)
                    logger.logged shouldBe null
                }
                randomString().let {
                    logger.fatal(it)
                    logger.logged shouldBe it
                }
            }
        }
    }

})