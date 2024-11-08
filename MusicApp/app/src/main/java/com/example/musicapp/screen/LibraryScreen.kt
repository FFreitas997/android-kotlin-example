package com.example.musicapp.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.viewmodel.MainViewModel

data class LibraryItem(@DrawableRes val icon: Int, val name: String)

@Composable
fun LibraryScreen(navController: NavHostController, viewModel: MainViewModel) {

    val libraryItems = listOf(
        LibraryItem(icon = R.drawable.baseline_playlist_play_24, name = "Playlist"),
        LibraryItem(icon = R.drawable.baseline_mic_24, name = "Artists"),
        LibraryItem(icon = R.drawable.baseline_album_24, name = "Albums"),
        LibraryItem(icon = R.drawable.baseline_music_note_24, name = "Songs"),
        LibraryItem(icon = R.drawable.baseline_sort_24, name = "Genres")
    )
    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    LazyColumn {
        items(libraryItems) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = it.name,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = it.name)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}