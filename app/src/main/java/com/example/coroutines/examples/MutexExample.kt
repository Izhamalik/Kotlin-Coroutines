package com.example.coroutines.examples

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Mutex protects shared mutable state across coroutines without blocking threads
 * the way synchronized does (suspending lock).
 */
object MutexExample : CoroutineExample {
    override val id = "mutex"
    override val title = "Mutex / shared state"
    override val description =
        "Mutex.withLock { } safely mutates shared state from concurrent coroutines."

    override suspend fun run(log: (String) -> Unit) {
        val mutex = Mutex()
        var counter = 0

        coroutineScope {
            repeat(100) {
                launch(Dispatchers.Default) {
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }

        log("Final counter = $counter (expected 100)")
    }
}
