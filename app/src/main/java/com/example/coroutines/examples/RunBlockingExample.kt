package com.example.coroutines.examples

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * runBlocking bridges blocking code and coroutines — it blocks the current thread
 * until the coroutine inside finishes. Prefer lifecycleScope / viewModelScope in Android UI.
 * Useful for main() and tests.
 */
object RunBlockingExample : CoroutineExample {
    override val id = "runBlocking"
    override val title = "runBlocking"
    override val description =
        "runBlocking { } blocks the calling thread until the block completes. " +
            "Use in tests/main; avoid on Android main thread for long work."

    override suspend fun run(log: (String) -> Unit) {
        log("runBlocking is for bridging from regular (non-suspend) code.")
        log("Demonstrating it on a background thread (never long-block Main):")

        val result = withContext(Dispatchers.Default) {
            // Imagine this function is a plain fun main() or a JUnit test:
            runBlocking {
                delay(200)
                "done via runBlocking on ${threadName()}"
            }
        }
        log(result)
        log("In Android UI use lifecycleScope.launch / viewModelScope.launch instead.")
    }
}
