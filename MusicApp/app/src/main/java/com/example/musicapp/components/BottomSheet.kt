package com.example.musicapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState, showBottomSheet: MutableState<Boolean>
) {
    if (showBottomSheet.value) {
        ModalBottomSheet(
            modifier = Modifier.height(300.dp),
            sheetState = sheetState,
            containerColor = colorResource(id = R.color.purple_200),
            onDismissRequest = { showBottomSheet.value = false }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.baseline_settings_24),
                        contentDescription = null
                    )
                    Text(text = "Settings")
                }
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.baseline_share_24),
                        contentDescription = null
                    )
                    Text(text = "Share")
                }
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.baseline_help_24),
                        contentDescription = null
                    )
                    Text(text = "Help")
                }
            }
        }
    }
}