package ktlogging.template

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import ktlogging.randomString

class TemplateTest : DescribeSpec({
    describe("message templating") {
        it("returns the supplied template if there are no named holes") {
            val template = randomString()
            val templated = template(template)

            templated.template shouldBe template
            templated.evaluated shouldBe template
            templated.items shouldHaveSize 0
        }
        it("evaluates a single named hole using the first supplied item") {
            val template = "User {Name} logged in"
            val name = randomString()
            val templated = template(template, name)

            templated.template shouldBe template
            templated.evaluated shouldBe "User $name logged in"
            templated.items shouldHaveSize 1
            templated.items shouldContain ("Name" to name)
        }
        it("evaluates multiple holes in a template") {
            val testTemplate = "User {Name} logged in from {IpAddress} at {LoginTime}"

            with(template(testTemplate, "Fred", "192.168.1.1", 1626091790484)) {
                template shouldBe testTemplate
                evaluated shouldBe "User Fred logged in from 192.168.1.1 at 1626091790484"
                items shouldContainExactly mapOf(
                    "Name" to "Fred",
                    "IpAddress" to "192.168.1.1",
                    "LoginTime" to "1626091790484",
                )
            }
        }
        it("ignores extra items provided") {
            with(template("User {Name} logged in", "Joe", "192.168.2.2")) {
                evaluated shouldBe "User Joe logged in"
                items shouldContainExactly mapOf("Name" to "Joe")
            }
        }
        it("only evaluates provided items") {
            with(template("User {Name} logged in from {IpAddress}", "Sue")) {
                evaluated shouldBe "User Sue logged in from {IpAddress}"
                items shouldContainExactly mapOf("Name" to "Sue")
            }
        }
    }

    describe("extracting holes") {
        it("returns only texts if there are no holes") {
            val extracted = extractHoles("No holes here")
            extracted shouldHaveSize 1
            extracted shouldContain (TextOrHole.TEXT to "No holes here")
        }
        it("returns a hole in the middle of a template") {
            extractHoles("User {Name} logged in") shouldContainInOrder listOf(
                TextOrHole.TEXT to "User ",
                TextOrHole.HOLE to "Name",
                TextOrHole.TEXT to " logged in",
            )
        }
        it("returns a hole at the beginning of a template") {
            extractHoles("{User} was logged in") shouldContainInOrder listOf(
                TextOrHole.HOLE to "User",
                TextOrHole.TEXT to " was logged in",
            )
        }
        it("returns a hole at the end of a template") {
            extractHoles("Login from {User}") shouldContainInOrder listOf(
                TextOrHole.TEXT to "Login from ",
                TextOrHole.HOLE to "User",
            )
        }
        it("returns multiple holes throughout a template") {
            extractHoles("User {Name} logged in from {IpAddress} at {LoginTime}") shouldContainInOrder listOf(
                TextOrHole.TEXT to "User ",
                TextOrHole.HOLE to "Name",
                TextOrHole.TEXT to " logged in from ",
                TextOrHole.HOLE to "IpAddress",
                TextOrHole.TEXT to " at ",
                TextOrHole.HOLE to "LoginTime",
            )
        }
    }

})
