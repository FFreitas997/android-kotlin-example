package com.example.recipeapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.recipeapp.data.Category
import com.example.recipeapp.screen.CategoryDetailScreen
import com.example.recipeapp.screen.CategoryScreen
import com.example.recipeapp.screen.Screen
import com.example.recipeapp.viewmodel.CategoryViewModel

@Composable
fun RecipeApp(navController: NavHostController, modifier: Modifier = Modifier) {
    val categoryViewModel: CategoryViewModel = viewModel()

    val categoryState by categoryViewModel.categoriesState

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Screen.CategoryScreen.route,
    ) {
        composable(route = Screen.CategoryScreen.route) {
            CategoryScreen(categoriesState = categoryState) {
                // Navigate to detail screen with object data
                navController.currentBackStackEntry?.savedStateHandle?.set("category", it)
                navController.navigate(Screen.CategoryDetailScreen.route)
            }
        }

        composable(route = Screen.CategoryDetailScreen.route) {
            val category = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Category>("category") ?: Category(
                "-1 ",
                "no name",
                "no thumbnail",
                "no description"
            )

            CategoryDetailScreen(category = category)
        }
    }
}