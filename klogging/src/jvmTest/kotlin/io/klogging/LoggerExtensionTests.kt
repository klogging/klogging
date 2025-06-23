package io.klogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe

class LoggerExtensionTests :
    DescribeSpec({
        describe("`Klogger.toNoCoLogger()` extension function") {
            it("copies the source logger name if a new name is not specified") {
                val sourceName = randomString()
                logger(sourceName).toNoCoLogger().name shouldBe sourceName
                logger(sourceName).toNoCoLogger(randomString() to randomString()).name shouldBe sourceName
            }
            it("uses a new name if specified") {
                val sourceName = randomString()
                val sourceLogger = logger(sourceName)
                val newName = randomString()
                sourceLogger.toNoCoLogger(newName).name shouldBe newName
                sourceLogger.toNoCoLogger(newName, randomString() to randomString()).name shouldBe newName
            }
            it("adds new logger context items if specified") {
                val events = savedEvents()
                val contextItems = randomString() to randomString()

                logger(randomString()).toNoCoLogger(contextItems).info(randomString())
                logger(randomString()).toNoCoLogger(randomString(), contextItems).info(randomString())

                events.first().items shouldContain contextItems
                events.last().items shouldContain contextItems
            }
        }
        describe("`NoCoLogger.toKlogger()` extension function") {
            it("copies the source logger name if a new name is not specified") {
                val sourceName = randomString()
                noCoLogger(sourceName).toKlogger().name shouldBe sourceName
                noCoLogger(sourceName).toKlogger(randomString() to randomString()).name shouldBe sourceName
            }
            it("uses a new name if specified") {
                val sourceName = randomString()
                val sourceLogger = noCoLogger(sourceName)
                val newName = randomString()
                sourceLogger.toKlogger(newName).name shouldBe newName
                sourceLogger.toKlogger(newName, randomString() to randomString()).name shouldBe newName
            }
            it("adds new logger context items if specified") {
                val events = savedEvents()
                val contextItems = randomString() to randomString()

                noCoLogger(randomString()).toKlogger(contextItems).info(randomString())
                noCoLogger(randomString()).toKlogger(randomString(), contextItems).info(randomString())

                events.first().items shouldContain contextItems
                events.last().items shouldContain contextItems
            }
        }
    })
