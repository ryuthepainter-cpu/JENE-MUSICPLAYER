package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.AlbumItem

@Composable
fun AlbumsScreen(viewModel: MainViewModel) {
    val songs by viewModel.allSongs.collectAsStateWithLifecycle()
    
    val albums = remember(songs) {
        songs.groupBy { it.album }
            .map { (albumName, albumSongs) ->
                AlbumInfo(
                    name = albumName,
                    artist = albumSongs.first().artist,
                    artData = albumSongs.first().data,
                    trackCount = albumSongs.size
                )
            }.sortedBy { it.name }
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                text = "Albums",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
            )
        }
        
        items(albums) { album ->
            AlbumItem(
                albumName = album.name,
                artistName = album.artist,
                albumArtData = album.artData,
                trackCount = album.trackCount,
                onClick = { /* TODO: Navigate to Album Details */ }
            )
        }
    }
}

data class AlbumInfo(
    val name: String,
    val artist: String,
    val artData: String,
    val trackCount: Int
)
