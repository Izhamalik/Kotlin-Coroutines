package com.example.coroutines.examples

/** Ordered list of every concept demo shown in the app. */
object CoroutineCatalog {
    val all: List<CoroutineExample> = listOf(
        SuspendExample,
        LaunchExample,
        AsyncExample,
        RunBlockingExample,
        DispatchersExample,
        WithContextExample,
        JobExample,
        CancellationExample,
        TimeoutExample,
        ExceptionHandlingExample,
        ScopeExample,
        ContextExample,
        ParallelDecompositionExample,
        ChannelExample,
        FlowExample,
        MutexExample,
    )
}
