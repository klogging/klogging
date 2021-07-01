package klogger.context

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
            it("an outer scope LogContext is available after an inner scope has finished") {
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
                    coroutineContext[LogContext]?.get("colour") shouldBe null
                    launch(logContext("colour" to "green")) {
                        coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                        coroutineContext[LogContext]?.get("colour") shouldBe "green"
                    }
                    coroutineContext[LogContext]?.get("scope") shouldBe "outer"
                    coroutineContext[LogContext]?.get("colour") shouldBe null
                }
            }
        }
        describe("inside coroutine scope") {
            it("items can be added after a coroutine has started") {
                launch(logContext()) {
                    val context = coroutineContext[LogContext]
                    context shouldNotBe null
                    context?.get("colour") shouldBe null

                    addToContext("colour" to "black")
                    context?.get("colour") shouldBe "black"
                }
                coroutineContext[LogContext] shouldBe null
            }
            it("items can be removed after a coroutine has started") {
                launch(logContext("colour" to "yellow")) {
                    val context = coroutineContext[LogContext]
                    context shouldNotBe null
                    context?.get("colour") shouldBe "yellow"

                    removeFromContext("colour")
                    context?.get("colour") shouldBe null

                    addToContext("animal" to "cat")
                    removeFromContext("animal")
                    context?.get("animal") shouldBe null
                }
            }
        }
    }
})