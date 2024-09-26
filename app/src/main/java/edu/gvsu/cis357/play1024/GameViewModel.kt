package edu.gvsu.cis357.play1024

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    private val _numbers = MutableLiveData<List<String>>()
    val numbers: LiveData<List<String>> get() = _numbers

    fun resetGame() {
        TODO()
    }
    fun doSwipe(dir: Swipe) {
        when(dir) {
            Swipe.UP -> _numbers.value = (1..16).toList().map { "^" }
            Swipe.DOWN -> _numbers.value = (1..16).toList().map { "v" }
            Swipe.LEFT -> _numbers.value = (1..16).toList().map { "<" }
            Swipe.RIGHT -> _numbers.value = (1..16).toList().map { ">" }

        }
    }
}