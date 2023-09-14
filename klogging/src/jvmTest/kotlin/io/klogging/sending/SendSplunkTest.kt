package io.klogging.sending

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.util.UUID

class SendSplunkTest : DescribeSpec({
    describe("`SplunkEndpoint`") {
        it("`toString()` masks `hecToken` values with ******") {
            val token = UUID.randomUUID().toString()
            val endpoint = SplunkEndpoint("https://localhost:8088", token)
            with(endpoint.toString()) {
                shouldNotContain(token)
                shouldContain("********")
            }
        }
    }
})