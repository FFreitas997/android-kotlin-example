package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.screen.CategoryDetailScreen
import com.example.recipeapp.screen.CategoryScreen
import com.example.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBarLayout() }
                ) {
                    val navController = rememberNavController()
                    RecipeApp(navController = navController, modifier = Modifier.padding(it))
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarLayout() =
    TopAppBar(
        title = { Text(text = "Recipe App") },
        colors = topAppBarColors(
            containerColor = Color(0xFF8DB1DF),
            titleContentColor = Color.White,
        ),
    )