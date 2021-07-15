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
            |"@m":"${event.message}",
            |"@mt":"${event.message}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}"
            |}""".trimMargin().replace("\n", "")
        }
        it("includes @x if `stackTrace` is present") {
            val ts = timestampNow()
            val trace = randomString()
            val event = LogEvent(newId(), ts, "test.local", "Test", Level.INFO, null, "Message", trace, mapOf())

            event.toClef() shouldBe """{
            |"@t":"${iso(event.timestamp)}",
            |"@m":"${event.message}",
            |"@mt":"${event.message}",
            |"@l":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"@x":"${event.stackTrace}"
            |}""".trimMargin().replace("\n", "")
        }
    }
})
