package klogger.clef

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.iso
import klogger.events.newId
import klogger.timestampNow

class ClefEventTest : DescribeSpec({
    describe("Creating CLEF event JSON") {
        val ts = timestampNow()
        val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, "Message", null, mapOf())

        event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@m":"${event.message}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.name}"
            |}""".trimMargin().replace("\n", "")
    }
})
