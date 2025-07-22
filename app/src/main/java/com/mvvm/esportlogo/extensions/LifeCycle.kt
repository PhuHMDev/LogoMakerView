package com.mvvm.esportlogo.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> FragmentActivity.observeWithLifeCycle(flow: Flow<T>, observer: (T) -> Unit = {}) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(observer)
        }
    }
}

fun <T> FragmentActivity.observeImmediately(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    block(stateFlow.value)
    launch { stateFlow.collect(block) }
}

private fun Fragment.launch(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch { repeatOnLifecycle(state, block) }
}


fun <T> Fragment.observeWithLifeCycle(flow: Flow<T>, observer: (T) -> Unit = {}): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(observer)
        }
    }
}

fun <T> Fragment.observeImmediately(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    block(stateFlow.value)
    launch { stateFlow.collect(block) }
}

private fun FragmentActivity.launch(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch { repeatOnLifecycle(state, block) }
}