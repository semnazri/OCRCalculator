package com.semnazri.ocrcalculator.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.semnazri.ocrcalculator.util.StateWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<Event, State> : ViewModel(), CoroutineScope {

    abstract fun callEvent(event: Event)

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val _state = MutableLiveData<StateWrapper<State>>()
    val state: LiveData<StateWrapper<State>> = _state

    protected fun setState(state: State) {
        _state.value = StateWrapper(state)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}