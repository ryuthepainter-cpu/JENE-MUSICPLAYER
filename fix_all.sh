cat << 'SONG' > app/src/main/java/com/jene/music/ui/components/SongListItem.kt
package com.jene.music.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jene.music.data.Song

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JeneArtwork(
            model = song.artworkUri ?: song.data,
            modifier = Modifier.size(56.dp),
            cornerRadius = 8.dp
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        var showMenu by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                if (onAddToPlaylist != null) {
                    DropdownMenuItem(
                        text = { Text("Add to playlist") },
                        onClick = { showMenu = false; onAddToPlaylist() }
                    )
                }
            }
        }
    }
}
SONG

sed -i '/var songToAddToPlaylist by remember { mutableStateOf<Song?>/d' app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt
sed -i '/if (songToAddToPlaylist != null) {/,/    }/d' app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt
sed -i '/fun SongsTab(viewModel: MainViewModel) {/a \
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }\n    if (songToAddToPlaylist != null) {\n        AddToPlaylistDialog(song = songToAddToPlaylist!!, viewModel = viewModel, onDismiss = { songToAddToPlaylist = null })\n    }' app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt

cat << 'ALBUM' > app/src/main/java/com/jene/music/ui/screens/AlbumDetailScreen.kt
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
import com.jene.music.data.Song

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
                                    viewModel.musicServiceConnection.setShuffleModeEnabled(false)
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
ALBUM

# Fix SearchScreen.kt 
sed -i '/var songToAddToPlaylist by remember { mutableStateOf<Song?>/d' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt
sed -i '/if (songToAddToPlaylist != null) {/,/    }/d' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt
sed -i '/val searchResults by/a \
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }\n    if (songToAddToPlaylist != null) {\n        AddToPlaylistDialog(song = songToAddToPlaylist!!, viewModel = viewModel, onDismiss = { songToAddToPlaylist = null })\n    }' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt

