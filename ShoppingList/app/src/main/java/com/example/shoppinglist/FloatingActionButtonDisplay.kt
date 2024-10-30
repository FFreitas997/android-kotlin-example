package com.example.shoppinglist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

@Composable
fun FloatingActionButtonDisplay(isDialogOpen: MutableState<Boolean>) {

    FloatingActionButton(
        onClick = { isDialogOpen.value = true },
        containerColor = Color(0xFFED96A7),
        contentColor = Color.White
    ) { Icon(Icons.Default.Add, contentDescription = "Add") }

}