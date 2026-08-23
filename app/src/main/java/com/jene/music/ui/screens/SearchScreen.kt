package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.data.Song
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.SongListItem
import com.jene.music.ui.components.AddToPlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel) {
    var query by remember { mutableStateOf("") }
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle(emptyList())

    val searchResults = remember(query, allSongs) {
        if (query.isBlank()) {
            emptyList()
        } else {
            allSongs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
    }

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(
            song = songToAddToPlaylist!!,
            viewModel = viewModel,
            onDismiss = { songToAddToPlaylist = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            placeholder = { Text("Search songs, artists, albums") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            items(searchResults, key = { it.id }) { song ->
                SongListItem(
                    song = song,
                    onClick = { viewModel.playSong(song, searchResults) },
                    onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onAddToPlaylist = { songToAddToPlaylist = song }
                )
            }
        }
    }
}
