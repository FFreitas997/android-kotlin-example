package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.shoppinglist.ui.theme.ShoppingListTheme

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
                ) { ShoppingListApp(isDialogOpen, it) }
            }

        }
    }

}



