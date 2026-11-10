package com.burtwelding.fabapp.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.reflect.typeOf

/**
 * Shows [default] while loading. Assignments save asynchronously by default.
 * With [blocking], assignments wait for the save and update this binding before returning.
 * [onError] receives read and write failures. By default, failures are rethrown.
 */
@Composable
inline fun <reified T : Any> rememberStorage(
    key: String,
    default: T,
    blocking: Boolean = false,
    noinline onError: (Exception) -> Unit = { throw it },
): MutableState<T> {
    val type = typeOf<T>()
    if (LocalInspectionMode.current) return remember(key, type, default) { mutableStateOf(default) }

    val stored = remember(key, type, default) { Storage.value(key, default) }
    val state = remember(stored) { mutableStateOf(default, referentialEqualityPolicy()) }
    val blockingWrites = remember(stored) { mutableLongStateOf(0L) }
    val blockingMode = rememberUpdatedState(blocking)
    val reportError = rememberUpdatedState(onError)

    LaunchedEffect(stored) {
        Storage.changes(stored.key).collect {
            try {
                val writesBeforeRead = blockingWrites.longValue
                val saved = stored.get()
                // A read started before a blocking assignment can complete after it.
                if (blockingWrites.longValue == writesBeforeRead) state.value = saved
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                reportError.value(error)
            }
        }
    }

    val scope = rememberCoroutineScope()
    return remember(stored, scope) {
        object : MutableState<T> {
            private val writes = Mutex()

            override var value: T
                get() = state.value
                set(next) {
                    if (blockingMode.value) {
                        try {
                            runBlocking { writes.withLock { stored.set(next) } }
                            blockingWrites.longValue++
                            state.value = next
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            reportError.value(error)
                        }
                        return
                    }

                    scope.launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
                        try {
                            // Finish writes already accepted before the composable leaves.
                            withContext(NonCancellable) {
                                writes.withLock {
                                    // Reserve write order before yielding off the caller's thread.
                                    yield()
                                    stored.set(next)
                                }
                            }
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            scope.launch { reportError.value(error) }
                        }
                    }
                }

            override fun component1(): T = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}
