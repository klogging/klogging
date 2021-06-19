package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TimestampTest : DescribeSpec({

    describe("Log event timestamp") {
        it("renders seconds since the epoch and nanoseconds") {
            Timestamp(1624106509, 123456789).toString() shouldBe "1624106509.123456789"
        }
        it("renders nanoseconds with leading zeros") {
            Timestamp(1624106509, 12345).toString() shouldBe "1624106509.000012345"
            Timestamp(1624106509, 246).toString() shouldBe "1624106509.000000246"
        }
    }

})
