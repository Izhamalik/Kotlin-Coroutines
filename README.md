# Kotlin Coroutines Lab

Android app that demonstrates the main Kotlin Coroutines concepts with runnable examples. Open the app, tap **Run** on a concept, and read the output in the screen plus Logcat (`CoroutineDemo`).

## How to run

1. Open the project in Android Studio or Cursor.
2. Sync Gradle (coroutines come from `kotlinx-coroutines-core` and `kotlinx-coroutines-android`).
3. Run the `app` module on a device or emulator.
4. Each **Run** button executes the matching file under `app/src/main/java/com/example/coroutines/examples/`.

## What is a coroutine?

A **coroutine** is a lightweight unit of concurrency. It can **suspend** (pause) without blocking a thread, then resume later. Thousands of coroutines can share a small thread pool.

Compared with threads:

| Threads | Coroutines |
| --- | --- |
| OS-managed, relatively expensive | Library-managed, cheap |
| Blocking wait occupies a thread | `delay` / `await` free the thread |
| Hard to cancel as a tree | Structured concurrency (parent/child Jobs) |

**Structured concurrency** means a parent coroutine does not complete until its children finish (or are cancelled). Failures and cancellation propagate along that tree unless you isolate them with `SupervisorJob` / `supervisorScope`.

---

## Concepts in this project

### 1. `suspend` functions — `SuspendExample.kt`

A `suspend` function can pause without blocking the thread. It may only be called from another `suspend` function or from a coroutine builder (`launch`, `async`, `runBlocking`, `withContext`, …).

`delay(500)` is the typical example: the coroutine waits 500 ms, but the thread can do other work.

```kotlin
suspend fun fetchUserName(): String {
    delay(500) // suspend, do not block
    return "Ada Lovelace"
}
```

Regular functions cannot call `suspend` functions directly.

---

### 2. `launch` — `LaunchExample.kt`

`launch` starts a **fire-and-forget** coroutine and returns a `Job`. Use it when you do not need a result.

```kotlin
coroutineScope {
    launch { delay(300); /* work A */ }
    launch { delay(150); /* work B */ }
    // coroutineScope waits until both children complete
}
```

Key points:

- Children run concurrently.
- `coroutineScope { }` is a suspend builder that waits for all children.
- Prefer `launch` for UI events, logging, fire-and-forget updates.

---

### 3. `async` / `await` — `AsyncExample.kt`

`async` starts a coroutine that **returns a value**. It yields `Deferred<T>`. Call `await()` to get the result (or the exception).

```kotlin
coroutineScope {
    val profile = async { loadProfile() }
    val posts = async { loadPosts() }
    val p = profile.await()
    val list = posts.await()
}
```

Start several `async` blocks first, then `await` them so the work overlaps. Do not `await` immediately after each `async` if you want parallelism.

---

### 4. `runBlocking` — `RunBlockingExample.kt`

`runBlocking` **blocks the current thread** until the coroutine inside finishes. It is a bridge from ordinary (non-suspend) code into coroutines.

Typical uses:

- `fun main()` sample programs
- JUnit tests

Avoid long `runBlocking` on the Android **main** thread (ANR). In the UI, use `lifecycleScope.launch`, `viewModelScope.launch`, or Compose `rememberCoroutineScope()`.

---

### 5. Dispatchers — `DispatchersExample.kt`

A **dispatcher** chooses which threads run the coroutine.

| Dispatcher | Use for |
| --- | --- |
| `Dispatchers.Main` | UI updates (Android main thread) |
| `Dispatchers.IO` | Disk, network, blocking I/O |
| `Dispatchers.Default` | CPU work (sorting, parsing, math) |
| `Dispatchers.Unconfined` | Starts on the caller thread; after first suspend may resume elsewhere. Rare / advanced |

The demo switches among these with `withContext` and logs `Thread.currentThread().name`.

---

### 6. `withContext` — `WithContextExample.kt`

`withContext(dispatcher) { }` switches context for a **block** and **returns a result**. Prefer this over a new `launch` when you only need to change threads.

Typical Android pattern:

1. Stay on Main in the UI / ViewModel.
2. `withContext(Dispatchers.IO)` for network/database.
3. `withContext(Dispatchers.Default)` for CPU-heavy parsing.
4. Resume on the original dispatcher automatically when the block ends.

---

### 7. `Job` and structured concurrency — `JobExample.kt`

Every coroutine has a **Job**. Jobs form a parent–child tree.

Useful APIs:

- `job.isActive` / `isCompleted` / `isCancelled`
- `job.cancel()` — request cancellation
- `job.join()` — wait until the job finishes
- `coroutineContext[Job]` — inspect the current job

Cancelling a parent cancels children. Completing a parent waits for children (`coroutineScope`).

---

### 8. Cancellation — `CancellationExample.kt`

Cancellation is **cooperative**. `cancel()` sets a flag; suspension points (`delay`, `await`, `yield`) check it and throw `CancellationException`.

In a tight CPU loop, call `ensureActive()` or `yield()`, or check `isActive`.

Rules:

- Always **rethrow** `CancellationException` after cleanup.
- Use `withContext(NonCancellable) { }` in `finally` if cleanup itself must suspend (`delay`, close a socket, write a file).

---

### 9. Timeout — `TimeoutExample.kt`

Limit how long a block may run:

- `withTimeout(ms) { }` — throws `TimeoutCancellationException` when time is up.
- `withTimeoutOrNull(ms) { }` — returns `null` instead of throwing.

```kotlin
val result = withTimeoutOrNull(200) {
    delay(500)
    "too slow"
} // result == null
```

---

### 10. Exception handling — `ExceptionHandlingExample.kt`

How failures surface depends on the builder:

| Builder | Exception behavior |
| --- | --- |
| `launch` | Propagates to the parent; can be caught by `CoroutineExceptionHandler` on the **root** of an unsupervised hierarchy |
| `async` | Stored in the `Deferred`; thrown when you `await()` |
| `coroutineScope` | If one child fails, siblings are cancelled |
| `supervisorScope` | One child’s failure does **not** cancel siblings |

```kotlin
val handler = CoroutineExceptionHandler { _, t ->
    log("Handler caught: ${t.message}")
}
```

`CoroutineExceptionHandler` is for uncaught exceptions from `launch`-style coroutines, not a replacement for `try/catch` around `await()`.

---

### 11. `CoroutineScope` — `ScopeExample.kt`

A **scope** owns coroutine lifetime: `Job` + dispatcher (and other context).

```kotlin
val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
scope.launch { /* work */ }
scope.cancel() // cancels all children
```

On Android, prefer:

- `lifecycleScope` (Activity / Fragment)
- `viewModelScope` (ViewModel)
- `rememberCoroutineScope()` (Compose)

Avoid `GlobalScope` in app code: it is not tied to a lifecycle, so work can leak after the screen is gone.

---

### 12. `CoroutineContext` — `ContextExample.kt`

`CoroutineContext` is a set of elements combined with `+`:

- `Job` — lifecycle
- `CoroutineDispatcher` — threads
- `CoroutineName` — debug name
- `CoroutineExceptionHandler` — uncaught exceptions

```kotlin
val named = CoroutineName("DemoCoroutine") + Dispatchers.Default
withContext(named) {
    coroutineContext[CoroutineName]?.name
}
```

Children inherit parent context; a child can override pieces (for example a different `CoroutineName`).

---

### 13. Parallel decomposition — `ParallelDecompositionExample.kt`

Independent suspend work should run **in parallel**, not one after another.

- Sequential: `loadA()` then `loadB()` ≈ 300 ms + 300 ms.
- Parallel: `async { loadA() }` and `async { loadB() }` then `await` both ≈ max(300, 300) ms.

Always wrap sibling `async` in `coroutineScope` (or an existing scope) so failures cancel the other work and nothing leaks.

---

### 14. Channels — `ChannelExample.kt`

A **Channel** is a **hot** pipe between coroutines (`send` / `receive`). Values are produced even if nobody is listening yet (unlike a cold `Flow`).

```kotlin
val channel = Channel<Int>(capacity = 3)
launch { channel.send(1); channel.close() }
launch { channel.consumeEach { /* consume */ } }
```

Capacity `3` is a buffered channel. Default capacity `0` is **rendezvous**: sender and receiver meet at the same time. Always `close()` when the producer is done so consumers finish.

---

### 15. `Flow` — `FlowExample.kt`

A **Flow** is a **cold** asynchronous stream. Nothing runs until `collect()`.

```kotlin
fun numbers(): Flow<Int> = flow {
    for (i in 1..4) {
        delay(80)
        emit(i)
    }
}

numbers()
    .onStart { /* before first value */ }
    .onEach { /* each raw emission */ }
    .map { it * 10 }
    .catch { e -> /* upstream errors */ }
    .collect { value -> /* consumer */ }
```

Useful operators: `map`, `filter`, `onEach`, `onStart`, `catch`, `debounce`, `flatMapLatest`. Use `StateFlow` / `SharedFlow` when you need hot, shared UI state (not shown as a separate demo here).

---

### 16. `Mutex` / shared state — `MutexExample.kt`

Coroutines on `Dispatchers.Default` can interleave. Mutating a shared `var` without protection loses updates.

`Mutex.withLock { }` is a **suspending** lock: it does not block a thread the way `synchronized` does.

```kotlin
val mutex = Mutex()
var counter = 0
mutex.withLock { counter++ }
```

The demo launches 100 concurrent increments; the final count is 100.

---

## Project layout

```
app/src/main/java/com/example/coroutines/
├── MainActivity.kt                 # Hosts the demo UI
├── examples/
│   ├── CoroutineExample.kt         # Shared contract + threadName()
│   ├── CoroutineCatalog.kt         # Ordered list of demos
│   └── *Example.kt                 # One file per concept
└── ui/
    └── CoroutineDemoScreen.kt      # Compose list + log panel
```

## Quick chooser

| I want to… | Use |
| --- | --- |
| Pause without blocking | `suspend` + `delay` / `await` |
| Start work, ignore the value | `launch` |
| Start work, need a value | `async` + `await` |
| Call coroutines from tests / `main` | `runBlocking` |
| Change threads and return a value | `withContext` |
| Bound lifetime to a screen / VM | `lifecycleScope` / `viewModelScope` |
| Stop work when the user leaves | `cancel` / cancel the scope |
| Fail after N milliseconds | `withTimeout` / `withTimeoutOrNull` |
| Isolate sibling failures | `supervisorScope` / `SupervisorJob` |
| Stream many values over time | `Flow` |
| Hand values between two coroutines | `Channel` |
| Protect a shared counter / list | `Mutex` |

## Official docs

- [Kotlin coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android coroutines](https://developer.android.com/kotlin/coroutines)
