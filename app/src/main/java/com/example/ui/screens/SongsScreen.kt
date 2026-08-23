package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.SongListItem

@Composable
fun SongsScreen(viewModel: MainViewModel) {
    val songs by viewModel.allSongs.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 48.dp, bottom = 120.dp)
    ) {
        item {
            Text(
                text = "Songs",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
        
        items(songs, key = { it.id }) { song ->
            SongListItem(
                song = song,
                onClick = { viewModel.playSong(song, songs) },
                onFavoriteClick = { viewModel.toggleFavorite(song) }
            )
        }
    }
}
