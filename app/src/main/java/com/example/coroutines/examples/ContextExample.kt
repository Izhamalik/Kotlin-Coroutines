package com.example.coroutines.examples

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * CoroutineContext is a set of elements: Job, Dispatcher, CoroutineName, exception handler…
 * Combined with the + operator.
 */
object ContextExample : CoroutineExample {
    override val id = "context"
    override val title = "CoroutineContext"
    override val description =
        "Context = Job + Dispatcher + Name + … Combined with +. Inspect via coroutineContext."

    override suspend fun run(log: (String) -> Unit) {
        val named = CoroutineName("DemoCoroutine") + Dispatchers.Default
        withContext(named) {
            log("Name=${coroutineContext[CoroutineName]?.name}")
            log("Job=${coroutineContext[Job]}")
            log("Dispatcher part visible via thread: ${threadName()}")

            launch(CoroutineName("Child")) {
                delay(50)
                log("Child name=${coroutineContext[CoroutineName]?.name}")
            }
        }
        log("Context demo done")
    }
}
