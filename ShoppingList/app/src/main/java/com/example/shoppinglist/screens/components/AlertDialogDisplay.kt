package com.example.shoppinglist.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.data.GeocodingResult

@Composable
fun AlertDialogDisplay(
    address: GeocodingResult?,
    onSelectLocation: () -> Unit,
    onDismissRequest: () -> Unit,
    onClickDialogAdd: (String, String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        title = { Text(text = "Add Shopping Item") },
        text = {
            Column {

                Text(text = "Enter the name and quantity of the item you want to add to your shopping list")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Button(onClick = { onSelectLocation() }) { Text(text = "Add Location") }

                if (address != null) {
                    Text(text = "Address: ${address.formatted_address}", modifier = Modifier.padding(top = 8.dp))
                }

            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(onClick = {
                    onClickDialogAdd(name, quantity)
                    name = ""
                    quantity = ""
                }) { Text(text = "Add") }

                Button(onClick = onDismissRequest) { Text(text = "Cancel") }

            }

        }
    )

}