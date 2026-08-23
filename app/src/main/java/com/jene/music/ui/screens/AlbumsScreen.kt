package com.jene.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.AlbumItem

@Composable
fun AlbumsScreen(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit) {
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums, key = { it.name + it.artist }) { album ->
            AlbumItem(
                albumName = album.name,
                artistName = album.artist,
                albumArtData = album.artworkUri ?: album.songs.firstOrNull()?.data,
                trackCount = album.trackCount,
                onClick = { onNavigateToAlbum(album.name, album.artist) }
            )
        }
    }
}
