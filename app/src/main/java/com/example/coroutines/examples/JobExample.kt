package com.example.coroutines.examples

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

/**
 * Job represents the lifecycle of a coroutine.
 * Parent jobs cancel/wait for children (structured concurrency).
 */
object JobExample : CoroutineExample {
    override val id = "job"
    override val title = "Job & structured concurrency"
    override val description =
        "Every coroutine has a Job. Parents wait for children; cancelling a parent cancels children."

    override suspend fun run(log: (String) -> Unit) {
        coroutineScope {
            val parentJob = coroutineContext[Job]
            log("Parent job: $parentJob")

            val child = launch {
                repeat(5) { i ->
                    delay(100)
                    log("Child tick $i (active=$isActive)")
                }
            }

            delay(250)
            log("Cancelling child…")
            child.cancel()
            child.join()
            log("Child cancelled: ${child.isCancelled}, completed: ${child.isCompleted}")
        }
    }
}
