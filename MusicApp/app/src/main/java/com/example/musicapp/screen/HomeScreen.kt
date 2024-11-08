package com.example.musicapp.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicapp.viewmodel.MainViewModel
import com.example.musicapp.R

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val category = listOf(
        "Hits",
        "Happy",
        "Sad",
        "Romantic",
        "Party",
        "Workout",
        "Chill",
        "Focus",
        "Sleep",
        "Kids"
    )
    val group = listOf(
        "Recently Played",
        "Top Artists",
        "Top Albums",
        "Top Songs",
        "Top Playlists",
        "Top Podcasts"
    )

    LazyColumn {
        group.forEach {
            stickyHeader {
                Text(text = it, modifier = Modifier.padding(16.dp))
                LazyRow {
                    items(category) {
                        CategoryItem(category = it, R.drawable.baseline_apps_24)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: String, drawable: Int) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .size(200.dp),
        border = BorderStroke(3.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = category)
            Image(painter = painterResource(id = drawable), contentDescription = category)
        }
    }
}