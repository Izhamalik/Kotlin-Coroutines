package com.example.coroutines.examples

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/**
 * async starts a coroutine that produces a Deferred<T> result.
 * Call await() to get the value (or failure).
 */
object AsyncExample : CoroutineExample {
    override val id = "async"
    override val title = "async / await"
    override val description =
        "async { } returns Deferred<T>. Start concurrent work, then await results. " +
            "Ideal for parallel decomposition."

    override suspend fun run(log: (String) -> Unit) {
        coroutineScope {
            log("Starting parallel fetches…")

            val profile = async {
                delay(400)
                log("Profile loaded on ${threadName()}")
                "Profile(id=1)"
            }
            val posts = async {
                delay(250)
                log("Posts loaded on ${threadName()}")
                listOf("Post A", "Post B")
            }
            val friends = async {
                delay(300)
                log("Friends loaded on ${threadName()}")
                listOf("Bob", "Carol")
            }

            // Start all three now; await only when you need the values
            val p = profile.await()
            val postsList = posts.await()
            val friendsList = friends.await()
            log("Combined => profile=$p, posts=$postsList, friends=$friendsList")
        }
    }
}
