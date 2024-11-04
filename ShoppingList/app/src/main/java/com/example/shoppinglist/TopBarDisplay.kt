package com.example.shoppinglist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarDisplay() {
    TopAppBar(
        title = { Text(text = "Shopping List") },
        colors = topAppBarColors(
            containerColor = Color(0xFFED96A7),
            titleContentColor = Color.White,
        ),
    )
}