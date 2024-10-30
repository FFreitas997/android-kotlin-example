package com.example.navigationexample.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigationexample.ui.theme.NavigationExampleTheme

@Composable
fun SecondScreen(name: String, modifier: Modifier = Modifier, onNavigate: (name: String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Second screen", fontSize = 24.sp)
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Welcome $name!", fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = { onNavigate(name) }) { Text("Go to first screen") }
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    NavigationExampleTheme {
        SecondScreen("") {}
    }
}