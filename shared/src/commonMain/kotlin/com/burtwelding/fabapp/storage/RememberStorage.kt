package com.burtwelding.fabapp.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.reflect.typeOf

/**
 * Shows [default] while loading. Assignments save asynchronously and readers see committed values.
 * [onError] receives read and write failures. By default, failures are rethrown.
 */
@Composable
inline fun <reified T : Any> rememberStorage(
    key: String,
    default: T,
    noinline onError: (Exception) -> Unit = { throw it },
): MutableState<T> {
    val type = typeOf<T>()
    if (LocalInspectionMode.current) return remember(key, type, default) { mutableStateOf(default) }

    val stored = remember(key, type, default) { Storage.value(key, default) }
    val state = remember(stored) { mutableStateOf(default, referentialEqualityPolicy()) }
    val reportError = rememberUpdatedState(onError)

    LaunchedEffect(stored) {
        Storage.changes(stored.key).collect {
            try {
                state.value = stored.get()
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
                    scope.launch(start = CoroutineStart.UNDISPATCHED) {
                        try {
                            // Finish writes already accepted before the composable leaves.
                            withContext(NonCancellable) { writes.withLock { stored.set(next) } }
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            reportError.value(error)
                        }
                    }
                }

            override fun component1(): T = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}
