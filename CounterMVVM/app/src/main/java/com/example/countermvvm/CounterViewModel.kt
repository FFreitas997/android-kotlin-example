package com.example.countermvvm

import androidx.compose.runtime.IntState
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

class CounterViewModel() : ViewModel() {

    private val _repository: CounterRepository = CounterRepository()
    private val _counter = mutableIntStateOf(_repository.getCount().count)

    val counter: IntState = _counter.asIntState()

    fun increment() {
        _repository.incrementCounter()
        _counter.intValue = _repository.getCount().count
    }

    fun decrement() {
        if (_counter.intValue > 0) {
            _repository.decrementCounter()
            _counter.intValue = _repository.getCount().count
        }
    }

}