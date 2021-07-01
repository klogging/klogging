package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klogger.context.LogContext
import klogger.context.logContext
import kotlinx.coroutines.launch
import java.util.UUID

const val CORRELATION_ID = "correlationId"

class LogContextTest : DescribeSpec({
    describe("LogContext implementation of CoroutineContext") {
        it("a context item can be retrieved in a coroutine scope") {
            val id = UUID.randomUUID()
            launch(logContext(CORRELATION_ID to id)) {
                val context = coroutineContext[LogContext]
                context shouldNotBe null
                context?.get(CORRELATION_ID) shouldBe id
            }
        }
        it("a context item cannot be retrieved outside its coroutine scope") {
            launch(logContext()) {
                coroutineContext[LogContext] shouldNotBe null
            }
            coroutineContext[LogContext] shouldBe null
        }
        it("a LogContext is available after an inner coroutine scope has finished") {
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            launch(logContext(CORRELATION_ID to id1)) {
                coroutineContext[LogContext]?.get(CORRELATION_ID) shouldBe id1
                launch(logContext(CORRELATION_ID to id2)) {
                    coroutineContext[LogContext]?.get(CORRELATION_ID) shouldBe id2
                }
                coroutineContext[LogContext]?.get(CORRELATION_ID) shouldBe id1
            }
        }
        it("a LogContext in an inner coroutine scope contains items from a LogContext in an enclosing scope") {
            val id = UUID.randomUUID()
            launch(logContext(CORRELATION_ID to id)) {
                coroutineContext[LogContext]?.get(CORRELATION_ID) shouldBe id
                launch(logContext("this" to "that")) {
                    val context = coroutineContext[LogContext]
                    context?.get("this") shouldBe "that"
                    context?.get(CORRELATION_ID) shouldBe id
                }
                coroutineContext[LogContext]?.get(CORRELATION_ID) shouldBe id
                coroutineContext[LogContext]?.get("this") shouldBe null
            }
        }
    }
})