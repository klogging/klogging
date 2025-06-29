package io.klogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class LambdaArgumentTest :
    DescribeSpec({
        describe("Specifying event information in a lambda argument") {
            describe("Klogger with suspend functions that require coroutines") {
                describe("returns the lambda value as a message string") {
                    with(savedEvents()) {
                        val message = randomString()
                        logger("Simple event").info { message }
                        first().message shouldBe message
                    }
                }
                describe("does not emit an event if a lambda argument returns `Unit`") {
                    with(savedEvents()) {
                        logger("No events").info { /* Returns nothing */ }
                        shouldBeEmpty()
                    }
                }
            }
            describe("NoCoLogger with standard functions that do not require coroutines") {
                describe("returns the lambda value as a message string") {
                    with(savedEvents()) {
                        val message = randomString()
                        logger("Simple event").info { message }
                        first().message shouldBe message
                    }
                }
                describe("NoCoLogger does not emit an event if a lambda argument returns `Unit`") {
                    with(savedEvents()) {
                        logger("No events").info { /* Returns nothing */ }
                        shouldBeEmpty()
                    }
                }
            }
        }
    })
