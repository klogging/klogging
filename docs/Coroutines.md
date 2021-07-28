# Klogging and coroutines

Kotlin coroutines provide two blessings to Klogging.

## Contextual information management

The [`LogContext`](../src/commonMain/kotlin/io/klogging/context/LogContext.kt)
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
`input.runId` for that particular run. A couple of log events might look like this:

```json
{
  "timestamp": "2021-07-18T03:27:06.628560Z",
  "level": "INFO",
  "runId": "76CE907C",
  "message": "Starting logic"
}
{
  "timestamp": "2021-07-18T03:27:06.633872Z",
  "level": "INFO",
  "runId": "76CE907C",
  "message": "Finished logic"
}
```

## Asynchronous dispatching of log events

Coroutines make asynchronous dispatching easy.

**TBC**
