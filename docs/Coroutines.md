# KtLogging and coroutines

Kotlin coroutines provide two blessings to KtLogging.

## Contextual information management

The [`LogContext`](../src/commonMain/kotlin/ktlogging/context/LogContext.kt)
class is designed to hold a map of contextual information in a
coroutine context. For example:

```kotlin
suspend fun complexLogic(input: EssentialStuff): ValuableOutput {
    launch(logContext("runId" to input.runId)) {
        logger.info { "Starting logic" }
        
        // Do stuff, potentially logging events
        
        logger.info { "Finished logic" }
    }
}
```

The log events sent by any code within the scope of the launched
coroutine will contain a field called `runId` with the value of
`input.runId` for that particular run. That includes any other
coroutines launched by that one.

## Asynchronous dispatching of log events

Coroutines make asynchronous dispatching easy.

**TBC**
