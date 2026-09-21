package com.example.coroutines.examples

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

/**
 * Classic pattern: start multiple async children, await all, combine results.
 * Shows sequential vs parallel timing.
 */
object ParallelDecompositionExample : CoroutineExample {
    override val id = "parallel"
    override val title = "Parallel decomposition"
    override val description =
        "Run independent suspend work in parallel with async, then combine — much faster than sequential."

    override suspend fun run(log: (String) -> Unit) {
        val sequentialMs = measureTimeMillis {
            val a = loadA()
            val b = loadB()
            log("Sequential: $a + $b")
        }
        log("Sequential took ~${sequentialMs}ms")

        val parallelMs = measureTimeMillis {
            coroutineScope {
                val a = async { loadA() }
                val b = async { loadB() }
                log("Parallel: ${a.await()} + ${b.await()}")
            }
        }
        log("Parallel took ~${parallelMs}ms (should be ~max of the two)")
    }

    private suspend fun loadA(): String {
        delay(300)
        return "A"
    }

    private suspend fun loadB(): String {
        delay(300)
        return "B"
    }
}
