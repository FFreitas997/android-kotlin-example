package com.example.navigationexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigationexample.screen.FirstScreen
import com.example.navigationexample.screen.SecondScreen
import com.example.navigationexample.ui.theme.NavigationExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationExample(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "firstScreen/",
        modifier = modifier
    ) {
        composable("firstScreen/{name}") {
            FirstScreen(it.arguments?.getString("name") ?: "") { name -> navController.navigate("secondScreen/$name") }
        }
        composable("secondScreen/{name}") {
            SecondScreen(it.arguments?.getString("name") ?: "Unknown") {name -> navController.navigate("firstScreen/$name") }
        }
    }
}