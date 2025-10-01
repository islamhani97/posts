package com.islam97.android.apps.posts.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MviViewModel<S, I, E> : ViewModel() {
    protected val mutableEffectFlow: MutableSharedFlow<E> = MutableSharedFlow()
    val effectFlow: SharedFlow<E> get() = mutableEffectFlow

    protected abstract val mutableState: MutableStateFlow<S>
    val state: StateFlow<S> get() = mutableState

    abstract fun handleIntent(intent: I)
}