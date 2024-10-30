package com.example.shoppinglist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ShoppingListApp(isDialogOpen: MutableState<Boolean>, innerPadding: PaddingValues) {
    var listOfItems by remember { mutableStateOf(listOf<ShoppingItem>()) }

    val onEditItemList = { item: ShoppingItem ->
        listOfItems = listOfItems.map { it.copy(isEditing = it.id == item.id) }
    }

    val onDeleteItemList = { item: ShoppingItem ->
        listOfItems = listOfItems - item
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(listOfItems) { item ->
                if (item.isEditing)
                    ShoppingItemEditor(item){ name, quantity ->
                        listOfItems = listOfItems.map { it.copy(isEditing = false) }
                        val editedItem = listOfItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = name
                            it.quantity = quantity
                        }
                    }
                else
                    ShoppingListItem(item, onEditItemList, onDeleteItemList)
            }
        }

    }

    if (isDialogOpen.value) {

        val onDismissRequest = { isDialogOpen.value = false }

        AlertDialogDisplay(onDismissRequest) { name, quantity ->
            val newItem = ShoppingItem(
                id = listOfItems.size + 1,
                name = if (name.isNotBlank()) name else "Unknown",
                quantity = if (quantity.isNotBlank()) quantity.toIntOrNull() ?: 0 else 0
            )
            listOfItems = listOfItems + newItem
            isDialogOpen.value = false
        }
    }
}







