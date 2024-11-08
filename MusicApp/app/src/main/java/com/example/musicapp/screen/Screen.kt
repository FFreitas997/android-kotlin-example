package com.example.musicapp.screen

import androidx.annotation.DrawableRes
import com.example.musicapp.R

sealed class Screen(val title: String, val route: String) {

    sealed class BottomScreen(title: String, route: String, @DrawableRes val icon: Int) : Screen(title, route) {
        object Home : BottomScreen("Home", "home", R.drawable.baseline_home_24)
        object Browse : BottomScreen("Browse", "browse", R.drawable.baseline_manage_search_24)
        object Library : BottomScreen("Library", "library", R.drawable.baseline_local_library_24)
    }

    sealed class DrawerScreen(title: String, route: String, @DrawableRes val icon: Int) : Screen(title, route) {
        object Account : DrawerScreen("Account", "account", R.drawable.baseline_account_box_24)
        object Subscription : DrawerScreen("Subscription", "subscription", R.drawable.baseline_library_music_24)
        object AddAccount : DrawerScreen("Add Account", "add_account", R.drawable.baseline_person_add_alt_1_24)
    }
}