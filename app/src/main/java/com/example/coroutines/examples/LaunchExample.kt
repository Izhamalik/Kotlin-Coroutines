package com.example.coroutines.examples

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * launch starts a new coroutine that does not return a useful result (Job only).
 * Fire-and-forget work belongs here.
 */
object LaunchExample : CoroutineExample {
    override val id = "launch"
    override val title = "launch"
    override val description =
        "launch { } creates a fire-and-forget coroutine and returns a Job. " +
            "Use it when you do not need a return value."

    override suspend fun run(log: (String) -> Unit) {
        coroutineScope {
            log("Parent starts on ${threadName()}")

            val job1 = launch {
                delay(300)
                log("Child A finished on ${threadName()}")
            }
            val job2 = launch {
                delay(150)
                log("Child B finished on ${threadName()}")
            }

            log("Both children launched (jobs: ${job1.isActive}, ${job2.isActive})")
            // coroutineScope waits until all children complete
        }
        log("Parent done — structured concurrency waited for children")
    }
}
