package ktlogging.clef

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import ktlogging.events.Level
import ktlogging.events.LogEvent
import ktlogging.events.iso
import ktlogging.events.newId
import ktlogging.randomString
import ktlogging.timestampNow

class ClefEventTest : DescribeSpec({
    describe("Creating CLEF event JSON") {
        it("omits @x if `stackTrace` is null") {
            val ts = timestampNow()
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

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
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", trace, mapOf())

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
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

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
                newId(), ts, "test.local", "Test", Level.INFO, "Id={Id}", "Id={Id}", null,
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
