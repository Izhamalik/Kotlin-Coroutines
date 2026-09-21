package com.example.coroutines.examples

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * Flow is cold asynchronous streams. Nothing runs until collect().
 * Operators: map, filter, onEach, catch, etc.
 */
object FlowExample : CoroutineExample {
    override val id = "flow"
    override val title = "Flow"
    override val description =
        "Flow is a cold stream of values. Collect triggers emission; operators transform data."

    override suspend fun run(log: (String) -> Unit) {
        numbers()
            .onStart { log("Flow started") }
            .onEach { log("Emit $it") }
            .map { it * 10 }
            .catch { e -> log("Caught in flow: ${e.message}") }
            .collect { value ->
                log("Collect $value on ${threadName()}")
            }
        log("Flow completed")
    }

    private fun numbers(): Flow<Int> = flow {
        for (i in 1..4) {
            delay(80)
            emit(i)
        }
    }
}
