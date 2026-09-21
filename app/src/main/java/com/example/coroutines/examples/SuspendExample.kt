package com.example.coroutines.examples

import kotlinx.coroutines.delay


object SuspendExample : CoroutineExample {
    override val id = "suspend"
    override val title = "suspend functions"
    override val description =
        "A suspend function can delay/await without blocking the thread. " +
            "It must be called from a coroutine or another suspend function."

    override suspend fun run(log: (String) -> Unit) {
        log("Calling suspend work on ${threadName()}")
        val result = fetchUserName()
        log("Got result: $result on ${threadName()}")
    }

    private suspend fun fetchUserName(): String {
        // delay is a suspend function — frees the thread while waiting
        delay(500)
        return "Ada Lovelace"
    }
}
