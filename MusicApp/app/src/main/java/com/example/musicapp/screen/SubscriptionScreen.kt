package com.example.musicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicapp.viewmodel.MainViewModel

@Composable
fun SubscriptionScreen(navController: NavHostController, viewModel: MainViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Subscription Management", modifier = Modifier.padding(16.dp))
        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Column {
                Text(text = "Musical", modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Free Tier", modifier = Modifier.padding(start = 16.dp))
                    TextButton(onClick = {}) {
                        Row {
                            Text("See all Plans")
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
                Text("Get a Plan")
            }
        }
    }
}