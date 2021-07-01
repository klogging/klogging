package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klogger.context.LogContext
import klogger.context.logContext
import kotlinx.coroutines.launch

class LogContextTest : DescribeSpec({
    describe("LogContext implementation of CoroutineContext") {
        describe("context items") {
            it("can be retrieved inside the coroutine scope where it is defined") {
                launch(logContext("colour" to "blue")) {
                    coroutineContext[LogContext] shouldNotBe null
                    coroutineContext[LogContext]?.get("colour") shouldBe "blue"
                }
            }
            it("cannot be retrieved outside the coroutine scope where it is defined") {
                launch(logContext("colour" to "green")) {
                    coroutineContext[LogContext] shouldNotBe null
                    coroutineContext[LogContext]?.get("colour") shouldBe "green"
                }
                coroutineContext[LogContext] shouldBe null
            }
        }
        describe("nested coroutine scopes") {
            it("a LogContext is available after an inner coroutine scope has finished") {
                launch(logContext("scope" to "outer")) {
                    coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                    launch(logContext("scope" to "inner")) {
                        coroutineContext[LogContext]?.get("scope") shouldBe "inner"
                    }
                    coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                }
            }
            it("a LogContext in an inner coroutine scope contains items from a LogContext in an enclosing scope") {
                launch(logContext("scope" to "outer")) {
                    coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                    launch(logContext("colour" to "green")) {
                        coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                        coroutineContext[LogContext]?.get("colour") shouldBe "green"
                    }
                    coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                    coroutineContext[LogContext]?.get("colour") shouldBe null
                }
            }
        }
    }
})