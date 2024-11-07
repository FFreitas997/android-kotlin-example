package com.example.wishlist.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wishlist.screen.AddEditWishScreen
import com.example.wishlist.screen.HomeScreen
import com.example.wishlist.screen.Screen.AddScreen
import com.example.wishlist.screen.Screen.EditScreen
import com.example.wishlist.screen.Screen.HomeScreen
import com.example.wishlist.viewmodel.WishViewModel

@Composable
fun Navigation(
    viewModel: WishViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route
    ) {
        composable(HomeScreen.route) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }

        composable(AddScreen.route) {
            AddEditWishScreen(viewModel = viewModel, navController = navController)
        }

        composable(
            route = "${EditScreen.route}/{wishID}",
            arguments = listOf(navArgument("wishID") {
                type = NavType.LongType
                defaultValue = 0L
                nullable = false
            })
        ) {
            val wishID = it.arguments?.getLong("wishID") ?: 0L
            AddEditWishScreen(id = wishID, viewModel = viewModel, navController = navController)
        }
    }
}