package com.example.musicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicapp.viewmodel.MainViewModel
import com.example.musicapp.R

@Composable
fun AccountScreen(navController: NavHostController, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "account"
                )
                Column {
                    Text(text = "Francisco Freitas")
                    Text(text = "@FFreitas")
                }
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "edit")
            }
        }

        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_queue_music_24),
                contentDescription = "queue",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "My Music")
        }
        HorizontalDivider()
    }
}