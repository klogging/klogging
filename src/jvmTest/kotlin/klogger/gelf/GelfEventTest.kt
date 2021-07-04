package klogger.gelf

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import klogger.events.LogEvent
import timestampNow
import java.util.UUID

class GelfEventTest : DescribeSpec({
    describe("Creating GELF event JSON") {
        it("includes logger name as _name") {
            val id = UUID.randomUUID().toString()
            val ts = timestampNow()
            val event = LogEvent(id, ts, "Test", Level.INFO, "Message", mapOf())

            event.toGelf() shouldBe """{"version":"1.1","host":"$GELF_HOST","short_message":"Message","timestamp":$ts,"level":6,"_logger":"Test"}"""
        }
    }
})