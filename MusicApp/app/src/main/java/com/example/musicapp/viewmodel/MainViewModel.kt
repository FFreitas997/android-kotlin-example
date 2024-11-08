package com.example.musicapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.musicapp.screen.Screen

class MainViewModel: ViewModel() {

    private val _currentScreen = mutableStateOf<Screen>(Screen.BottomScreen.Home)
    val currentScreen: MutableState<Screen> = _currentScreen

    fun setCurrentScreen(screen: Screen) {
        _currentScreen.value = screen
    }
}