package com.example.coroutines.examples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CoroutineScope ties a Job + context together.
 * Android: lifecycleScope / viewModelScope are preferred over GlobalScope.
 */
object ScopeExample : CoroutineExample {
    override val id = "scopes"
    override val title = "CoroutineScope"
    override val description =
        "A scope owns coroutine lifetime. Cancel the scope to cancel all children. " +
            "Prefer lifecycleScope / viewModelScope over GlobalScope."

    override suspend fun run(log: (String) -> Unit) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        log("Custom scope created")

        scope.launch {
            delay(100)
            log("Work A done")
        }
        scope.launch {
            delay(150)
            log("Work B done")
        }

        delay(80)
        log("Cancelling entire scope…")
        scope.cancel()
        delay(100)
        log("After cancel — no further child logs expected")
    }
}
