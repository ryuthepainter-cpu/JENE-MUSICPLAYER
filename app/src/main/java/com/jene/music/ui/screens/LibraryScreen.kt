package com.jene.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.SongListItem
import com.jene.music.ui.components.AddToPlaylistDialog
import com.jene.music.data.model.Song
import com.jene.music.ui.screens.AlbumsScreen

@Composable
fun LibraryScreen(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Songs", "Albums")

    
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 16.dp)
        )
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        when (selectedTabIndex) {
            0 -> SongsTab(viewModel)
            1 -> AlbumsTab(viewModel, onNavigateToAlbum)
        }
    }
}

@Composable
fun SongsTab(viewModel: MainViewModel) {
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(song = songToAddToPlaylist!!, viewModel = viewModel, onDismiss = { songToAddToPlaylist = null })
    }
    val songs by viewModel.allSongs.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            SongListItem(
                song = song,
                onClick = { viewModel.playSong(song, songs) },
                onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onAddToPlaylist = { songToAddToPlaylist = song }
            )
        }
    }
}

@Composable
fun AlbumsTab(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit) {
    AlbumsScreen(viewModel, onNavigateToAlbum)
}
