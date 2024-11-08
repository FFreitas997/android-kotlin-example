package com.example.musicapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun AccountDialog(dialogOpen: MutableState<Boolean>) {

    if (dialogOpen.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(5.dp),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = { dialogOpen.value = false },
            confirmButton = {
                TextButton(onClick = { dialogOpen.value = false }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { dialogOpen.value = false }) { Text("Dismiss") }
            },
            title = { Text("Add Account") },
            text = {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier.padding(top = 16.dp),
                        value = "",
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        label = { Text("Email") }
                    )

                    TextField(
                        modifier = Modifier.padding(top = 16.dp),
                        value = "",
                        onValueChange = {},
                        label = { Text("Password") }
                    )
                }
            }
        )
    }

}