package io.klogging.rendering

import io.klogging.logEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EvalTemplateTest : DescribeSpec({
    describe("`LogEvent.evalTemplate()` extension function") {
        it("leaves the message unchanged if there are no items") {
            val message = "This is a {test} message"
            logEvent(message = message)
                .evalTemplate() shouldBe message
        }
        it("leaves the message unchanged if there are no placeholders") {
            val message = "This is a test message"
            logEvent(message = message, items = mapOf("test" to "TEST"))
                .evalTemplate() shouldBe message
        }
        it("replaces placeholders that match item keys with item values") {
            val message = "This is a {test} message"
            logEvent(message = message, items = mapOf("test" to "TEST", "test2" to "TEST2"))
                .evalTemplate() shouldBe "This is a TEST message"
        }
        it("does not replace placeholders if item keys are different cases") {
            val message = "This is a {test} message"
            logEvent(message = message, items = mapOf("TEST" to "TEST"))
                .evalTemplate() shouldBe message
        }
    }
})