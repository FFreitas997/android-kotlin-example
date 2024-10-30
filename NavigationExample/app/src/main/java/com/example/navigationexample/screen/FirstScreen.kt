package com.example.navigationexample.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigationexample.ui.theme.NavigationExampleTheme

@Composable
fun FirstScreen(returnName: String, modifier: Modifier = Modifier, onNavigate: (param: String) -> Unit) {
    var name by remember { mutableStateOf(returnName) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "First screen", fontSize = 24.sp)
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = { onNavigate(name) }) { Text("Go to second screen") }
    }
}

@Preview
@Composable
fun FirstScreenPreview() {
    NavigationExampleTheme {
        FirstScreen(""){}
    }
}