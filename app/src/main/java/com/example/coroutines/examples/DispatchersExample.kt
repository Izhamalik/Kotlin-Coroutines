package com.example.coroutines.examples

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dispatchers choose which thread pool a coroutine runs on.
 * Main = UI, IO = blocking I/O, Default = CPU work, Unconfined = advanced/rare.
 */
object DispatchersExample : CoroutineExample {
    override val id = "dispatchers"
    override val title = "Dispatchers"
    override val description =
        "Dispatchers.Main / IO / Default / Unconfined control which threads execute your code."

    override suspend fun run(log: (String) -> Unit) {
        log("Start on ${threadName()}")

        withContext(Dispatchers.Main) {
            log("Dispatchers.Main -> ${threadName()}")
        }
        withContext(Dispatchers.IO) {
            log("Dispatchers.IO -> ${threadName()}")
            // pretend disk/network
            Thread.sleep(50)
        }
        withContext(Dispatchers.Default) {
            log("Dispatchers.Default -> ${threadName()}")
            // pretend CPU work
            (1..1000).sum()
        }
        withContext(Dispatchers.Unconfined) {
            log("Dispatchers.Unconfined (start) -> ${threadName()}")
        }
        log("Back on ${threadName()}")
    }
}
