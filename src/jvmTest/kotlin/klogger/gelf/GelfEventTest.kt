package klogger.gelf

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.newId
import klogger.timestampNow

class GelfEventTest : DescribeSpec({
    describe("Creating GELF event JSON") {
        it("includes logger name as _name") {
            val ts = timestampNow()
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, "Message", null, mapOf())

            event.toGelf() shouldBe """{
                |"version":"1.1",
                |"host":"test.local",
                |"short_message":"Message",
                |"timestamp":${ts.graylogFormat()},
                |"level":${graylogLevel(Level.INFO)},
                |"_logger":"Test"
                |}""".trimMargin().replace("\n", "")
        }
    }
})