package com.example.coroutines.examples

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * withContext switches dispatcher for a block and returns a result.
 * Preferred over launching a new coroutine just to change threads.
 */
object WithContextExample : CoroutineExample {
    override val id = "withContext"
    override val title = "withContext"
    override val description =
        "withContext(dispatcher) { } switches context for a block and returns its result."

    override suspend fun run(log: (String) -> Unit) {
        log("UI/caller thread: ${threadName()}")

        val data = withContext(Dispatchers.IO) {
            log("Loading from network on ${threadName()}")
            delay(300)
            """{"user":"Izhar"}"""
        }

        val parsed = withContext(Dispatchers.Default) {
            log("Parsing JSON on ${threadName()}")
            delay(100)
            data.removePrefix("{").removeSuffix("}")
        }

        log("Ready for UI: $parsed on ${threadName()}")
    }
}
