package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEdit: (ShoppingItem) -> Unit,
    onDelete: (ShoppingItem) -> Unit
) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(border = BorderStroke(2.dp, Color(0xFFED96A7)), shape = RoundedCornerShape(20)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

            Text(text = item.name)

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Qty: ${item.quantity}")

        }

        Row {

            IconButton(onClick = { onEdit(item) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Delete")
            }

            IconButton(onClick = { onDelete(item) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }

        }

    }
}