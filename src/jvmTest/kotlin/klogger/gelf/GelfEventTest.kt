package klogger.gelf

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.newId
import klogger.randomString
import klogger.timestampNow

class GelfEventTest : DescribeSpec({
    describe("Creating GELF event JSON") {
        it("includes logger name as _logger") {
            val ts = timestampNow()
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

            event.toGelf() shouldBe """{
                |"version":"1.1",
                |"host":"${event.host}",
                |"short_message":"${event.message}",
                |"timestamp":${ts.graylogFormat()},
                |"level":${graylogLevel(Level.INFO)},
                |"_logger":"${event.logger}"
                |}""".trimMargin().replace("\n", "")
        }
        it("includes full_message with `stackTrace` if present") {
            val ts = timestampNow()
            val trace = randomString()
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", trace, mapOf())

            event.toGelf() shouldBe """{
                |"version":"1.1",
                |"host":"${event.host}",
                |"short_message":"${event.message}",
                |"full_message":"$trace",
                |"timestamp":${ts.graylogFormat()},
                |"level":${graylogLevel(Level.INFO)},
                |"_logger":"${event.logger}"
                |}""".trimMargin().replace("\n", "")
        }
    }
})