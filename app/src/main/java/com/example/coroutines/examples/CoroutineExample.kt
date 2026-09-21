package com.example.coroutines.examples

/**
 * Common contract for every coroutine concept demo.
 * [run] is a suspend function so demos can freely call other suspend APIs.
 */
interface CoroutineExample {
    val id: String
    val title: String
    val description: String
    suspend fun run(log: (String) -> Unit)
}

fun threadName(): String = Thread.currentThread().name
