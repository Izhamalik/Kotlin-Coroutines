package com.example.coroutines.examples

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/**
 * withTimeout throws TimeoutCancellationException; withTimeoutOrNull returns null.
 */
object TimeoutExample : CoroutineExample {
    override val id = "timeout"
    override val title = "Timeout"
    override val description =
        "withTimeout / withTimeoutOrNull limit how long a block may run."

    override suspend fun run(log: (String) -> Unit) {
        try {
            withTimeout(200) {
                log("Slow work started…")
                delay(500)
                log("This line should not run")
            }
        } catch (e: TimeoutCancellationException) {
            log("withTimeout threw: ${e.message}")
        }

        val result = withTimeoutOrNull(200) {
            delay(500)
            "too slow"
        }
        log("withTimeoutOrNull => $result (null means timed out)")

        val ok = withTimeoutOrNull(400) {
            delay(100)
            "finished in time"
        }
        log("withTimeoutOrNull => $ok")
    }
}
