package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import klogger.context.logContext
import kotlinx.coroutines.launch

class EventTest : DescribeSpec({
    describe("Constructing logging events") {
        describe("with context items") {
            it("does not include context items if there are none") {
                val logger = BaseLogger("EventTest")
                logger.info("Test message")
                BaseLogger.events.last().objects.size shouldBe 0
            }
            it("includes any items from the coroutine log context") {
                launch(logContext("colour" to "white")) {
                    val logger = BaseLogger("EventTest")
                    logger.info("Test message")
                    BaseLogger.events.last().objects shouldContain ("colour" to "white")
                }
            }
        }
    }
})