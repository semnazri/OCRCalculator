package com.semnazri.ocrcalculator.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

inline fun <T> LifecycleOwner.subscribeState(
    liveData: LiveData<StateWrapper<T>>,
    crossinline onEventUnhandled: (T) -> Unit
) {
    liveData.observe(this) {
        it?.getEventIfNotHandled()?.let(onEventUnhandled)
    }
}