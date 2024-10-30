package com.example.countermvvm

data class CounterModel(var count: Int)

class CounterRepository(initialState: Int = 0) {
    private val _counter = CounterModel(initialState)

    fun incrementCounter() {
        _counter.count++
    }

    fun decrementCounter() {
        if (_counter.count > 0) {
            _counter.count--
        }
    }

    fun getCount(): CounterModel { return _counter }
}
