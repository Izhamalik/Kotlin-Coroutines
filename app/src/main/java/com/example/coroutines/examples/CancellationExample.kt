package com.example.coroutines.examples

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

/**
 * Cooperative cancellation: cancel() sets a flag; suspension points check it.
 * Use ensureActive()/yield()/isActive in tight loops. NonCancellable for cleanup.
 */
object CancellationExample : CoroutineExample {
    override val id = "cancellation"
    override val title = "Cancellation"
    override val description =
        "Cancellation is cooperative. delay/await check it; CPU loops need ensureActive(). " +
            "Use NonCancellable for final cleanup."

    override suspend fun run(log: (String) -> Unit) {
        coroutineScope {
            val job = launch(Dispatchers.Default) {
                try {
                    var i = 0
                    while (i < 1_000_000) {
                        ensureActive() // throws if cancelled
                        if (i % 200_000 == 0) {
                            yield()
                            log("Working… i=$i active=$isActive")
                        }
                        i++
                    }
                } catch (e: CancellationException) {
                    log("Caught CancellationException — rethrowing after cleanup note")
                    throw e // always rethrow CancellationException
                } finally {
                    withContext(NonCancellable) {
                        delay(50) // cleanup that must complete
                        log("Cleanup finished under NonCancellable")
                    }
                }
            }

            delay(30)
            log("Requesting cancel…")
            job.cancel()
            job.join()
            log("Job finished. cancelled=${job.isCancelled}")
        }
    }
}
