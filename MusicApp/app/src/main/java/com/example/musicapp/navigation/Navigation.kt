package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.screen.AccountScreen
import com.example.musicapp.screen.BrowseScreen
import com.example.musicapp.screen.HomeScreen
import com.example.musicapp.screen.LibraryScreen
import com.example.musicapp.screen.Screen
import com.example.musicapp.screen.SubscriptionScreen
import com.example.musicapp.viewmodel.MainViewModel

@Composable
fun Navigation(modifier: Modifier, navController: NavHostController, viewModel: MainViewModel) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.BottomScreen.Home.route
    ) {

        composable(Screen.BottomScreen.Home.route) {
            HomeScreen(navController, viewModel)
        }

        composable(Screen.BottomScreen.Library.route) {
            LibraryScreen(navController, viewModel)
        }

        composable(Screen.BottomScreen.Browse.route) {
            BrowseScreen(navController, viewModel)
        }

        composable(Screen.DrawerScreen.Account.route) {
            AccountScreen(navController, viewModel)
        }

        composable(Screen.DrawerScreen.Subscription.route) {
            SubscriptionScreen(navController, viewModel)
        }

    }
}