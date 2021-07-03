package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Timestamp
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Box(val id: Int, val ts: Timestamp)

class TimestampTest : DescribeSpec({

    describe("Log event timestamp") {
        it("renders seconds since the epoch and nanoseconds") {
            Timestamp(1624106509, 123456789).toString() shouldBe "1624106509.123456789"
        }
        it("renders nanoseconds with leading zeros") {
            Timestamp(1624106509, 12345).toString() shouldBe "1624106509.000012345"
            Timestamp(1624106509, 246).toString() shouldBe "1624106509.000000246"
        }
        describe("JSON serialisation") {
            val box = Box(1, Timestamp(1624106509, 123456789))
            val boxJson = """{"id":1,"ts":"1624106509.123456789"}"""

            it("serialises to JSON as a decimal of seconds and nanoseconds") {
                Json.encodeToString(box) shouldBe boxJson
            }
            it("deserialises from JSON as decimal of seconds and nanoseconds") {
                Json.decodeFromString<Box>(boxJson) shouldBe box
            }
        }
    }
})
