package klogger.clef

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.iso
import timestampNow
import java.util.UUID

class ClefEventTest : DescribeSpec({
    describe("Creating CLEF event JSON") {
        val ts = timestampNow()
        val event = LogEvent(UUID.randomUUID().toString(), ts, "Test", Level.INFO, "Message", mapOf())

        event.toClef() shouldBe """{"@t":"${iso(event.timestamp)}","@m":"${event.message}","@l":"${event.level}","logger":"${event.name}"}"""
    }
})