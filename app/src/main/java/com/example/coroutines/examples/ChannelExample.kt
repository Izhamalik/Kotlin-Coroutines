package com.example.coroutines.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Channels transfer a stream of values between coroutines (hot, rendezvous by default).
 */
object ChannelExample : CoroutineExample {
    override val id = "channels"
    override val title = "Channels"
    override val description =
        "Channel is a hot communication primitive between coroutines (send / receive)."

    override suspend fun run(log: (String) -> Unit) {
        coroutineScope {
            val channel = Channel<Int>(capacity = 3)

            launch {
                for (x in 1..5) {
                    log("Sending $x")
                    channel.send(x)
                    delay(40)
                }
                channel.close()
                log("Producer closed channel")
            }

            launch {
                channel.consumeEach { value ->
                    log("Received $value on ${threadName()}")
                    delay(70)
                }
                log("Consumer done")
            }
        }
    }
}
