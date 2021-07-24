package io.klogging.clef

import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.iso
import io.klogging.randomString
import io.klogging.timestampNow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClefEventTest : DescribeSpec({
    describe("Creating CLEF event JSON") {
        it("omits @x if `stackTrace` is null") {
            val ts = timestampNow()
            val event = LogEvent(ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

            event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"@m":"${event.message}"
            |}""".trimMargin().replace("\n", "")
        }
        it("includes @x if `stackTrace` is present") {
            val ts = timestampNow()
            val trace = randomString()
            val event = LogEvent(ts, "test.local", "Test", Level.INFO, null, "Message", trace, mapOf())

            event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"@m":"${event.message}",
            |"@x":"${event.stackTrace}"
            |}""".trimMargin().replace("\n", "")
        }
        it("includes @m but not @mt if `template` is null") {
            val ts = timestampNow()
            val event = LogEvent(ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

            event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"@m":"${event.message}"
            |}""".trimMargin().replace("\n", "")
        }
        it("includes @mt but not @m if `template` is included") {
            val ts = timestampNow()
            val id = randomString()
            val event = LogEvent(
                ts, "test.local", "Test", Level.INFO, "Id={Id}", "Id={Id}", null,
                mapOf("Id" to id)
            )

            event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"Id":"$id",
            |"@mt":"${event.template}"
            |}""".trimMargin().replace("\n", "")
        }
    }
})
