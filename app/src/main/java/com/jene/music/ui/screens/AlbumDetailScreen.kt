package com.jene.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import com.jene.music.ui.components.SongListItem
import com.jene.music.ui.components.AddToPlaylistDialog
import com.jene.music.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    viewModel: MainViewModel,
    albumName: String,
    artistName: String,
    onBack: () -> Unit
) {
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val album = albums.find { it.name == albumName && it.artist == artistName }

    if (album == null) return
    
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(
            song = songToAddToPlaylist!!,
            viewModel = viewModel,
            onDismiss = { songToAddToPlaylist = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    JeneArtwork(
                        model = album.artworkUri ?: album.songs.firstOrNull()?.data,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                            .align(Alignment.CenterHorizontally),
                        cornerRadius = 16.dp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${album.artist} • ${if (album.year > 0) "${album.year} • " else ""}${album.trackCount} tracks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                if (album.songs.isNotEmpty()) {
                                    viewModel.playerController.setShuffleModeEnabled(false)
                                    viewModel.playSong(album.songs.first(), album.songs)
                                }
                            },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play")
                        }
                        
                        Button(
                            onClick = {
                                if (album.songs.isNotEmpty()) {
                                    viewModel.shuffleAndPlay(album.songs)
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shuffle", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            
            items(album.songs, key = { it.id }) { song ->
                SongListItem(
                    song = song,
                    onClick = { viewModel.playSong(song, album.songs) },
                    onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onAddToPlaylist = { songToAddToPlaylist = song }
                )
            }
        }
    }
}
