package com.example.wishlist.screen

sealed class Screen(val route: String) {

    object HomeScreen : Screen("home_screen")
    object AddScreen : Screen("add_screen")
    object EditScreen : Screen("edit_screen")
}