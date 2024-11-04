package com.example.shoppinglist

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog

import androidx.navigation.compose.rememberNavController
import com.example.shoppinglist.screens.LocationSelectionScreen
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import com.example.shoppinglist.utils.LocationUtils
import com.example.shoppinglist.viewmodels.LocationViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDialogOpen = remember { mutableStateOf(false) }
            ShoppingListTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBarDisplay() },
                    floatingActionButton = { FloatingActionButtonDisplay(isDialogOpen) }
                ) {
                    Navigation(Modifier.padding(it), isDialogOpen)
                }
            }

        }
    }
}

@Composable
fun Navigation(modifier: Modifier, dialogOpen: MutableState<Boolean>){
    val navController = rememberNavController()
    val viewModel: LocationViewModel = viewModel()
    val context: Context = LocalContext.current
    val locationUtils = LocationUtils(context)

    NavHost(navController = navController, startDestination = "shoppinglistscreen") {
        composable("shoppinglistscreen") {
            ShoppingListApp(
                modifier = modifier,

                locationUtils = locationUtils,
                navController = navController,
                viewModel = viewModel,
                context = context,
                address = viewModel.address.value.firstOrNull()?.formatted_address ?: "No Address",
                dialogOpen = dialogOpen
            )
        }
        dialog("locationscreen"){ backstackEntry ->
            viewModel.location.value?.let {
                LocationSelectionScreen(
                    location = it,
                    onLocationSelected = { location ->
                        viewModel.fetchAddress("${location.latitude},${location.longitude}")
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}



