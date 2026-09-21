package com.example.coroutines.examples

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * launch: exceptions propagate to parent / handler.
 * async: exceptions held until await().
 * supervisorScope: sibling failure does not cancel others.
 */
object ExceptionHandlingExample : CoroutineExample {
    override val id = "exceptions"
    override val title = "Exception handling"
    override val description =
        "CoroutineExceptionHandler for launch; await() surfaces async failures; " +
            "supervisorScope isolates sibling failures."

    override suspend fun run(log: (String) -> Unit) {
        val handler = CoroutineExceptionHandler { _, throwable ->
            log("Handler caught: ${throwable.message}")
        }

        // --- async: failure deferred until await ---
        try {
            coroutineScope {
                val deferred = async {
                    delay(50)
                    error("async failed")
                }
                log("async started; exception not thrown yet")
                deferred.await()
            }
        } catch (e: IllegalStateException) {
            log("await() threw: ${e.message}")
        }

        // --- supervisorScope: one child fails, sibling continues ---
        supervisorScope {
            launch(handler) {
                delay(30)
                error("child A boom")
            }
            launch {
                delay(80)
                log("child B still completed under supervisorScope")
            }
        }
        log("supervisorScope finished")
    }
}
