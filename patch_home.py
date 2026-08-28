import re

content = """package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jene.music.data.model.Song
import com.jene.music.data.model.Album
import com.jene.music.data.model.PlaylistWithSongs
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import com.jene.music.ui.components.JeneGlassButton
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlbum: (String, String) -> Unit
) {
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    val recentlyAdded by viewModel.recentlyAddedSongs.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.mostPlayedSongs.collectAsStateWithLifecycle()
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val playlists by viewModel.allPlaylists.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 100.dp, bottom = 120.dp, start = 20.dp, end = 20.dp)
        ) {
            item {
                GreetingText()
                Spacer(modifier = Modifier.height(28.dp))
            }
            
            if (allSongs.isEmpty()) {
                item {
                    EmptyLibraryPrompt(onScanLibrary = { viewModel.scanLibrary() })
                }
                return@LazyColumn
            }

            if (recentlyPlayed.isNotEmpty()) {
                item {
                    RecentlyPlayedSection(
                        songs = recentlyPlayed.take(10),
                        onPlaySong = { viewModel.playSong(it, recentlyPlayed) }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            if (recentlyAdded.isNotEmpty()) {
                item {
                    RecentlyAddedSection(
                        songs = recentlyAdded.take(5),
                        favoriteSongs = favoriteSongs,
                        onPlaySong = { viewModel.playSong(it, recentlyAdded) },
                        onToggleFavorite = { song, isFav ->
                            viewModel.toggleFavorite(song.copy(isFavorite = !isFav))
                        }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            if (albums.isNotEmpty()) {
                item {
                    AlbumsSection(
                        albums = albums.take(4),
                        onNavigateToAlbum = onNavigateToAlbum
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            if (playlists.isNotEmpty()) {
                item {
                    PlaylistsSection(
                        playlists = playlists
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
        
        HomeTopBar(onNavigateToSettings = onNavigateToSettings)
    }
}

@Composable
private fun HomeTopBar(onNavigateToSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Color(0xCC141313))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2cbVvO7QSLvTEflZCwMtPKdQ8xvbqoeuf5pYtFogOxZKKIbqpBYfQpM7GpQmz_ALMfDSB1LRAJIz4cK4tDo2thVzxzcI0Mxh0tj90RZew0IjukB1E04QTeBTXopUq08To4RSsXcv-nGydid1usbsuG8_URPP_gCagmx0yIB_U4Hg2aOnz59cYvOv2w5ZGnvqzWHFYZDJ6iY86mIZf6vfNQTS_EQX2jjERwSkHtFU4IMobTiYz37Q",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Jene",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.02).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onNavigateToSettings) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun GreetingText() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    Text(
        text = greeting,
        style = MaterialTheme.typography.displayLarge.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.02).sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun RecentlyPlayedSection(songs: List<Song>, onPlaySong: (Song) -> Unit) {
    Text(
        text = "Recently Played",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(songs, key = { it.id }) { song ->
            Column(
                modifier = Modifier
                    .width(112.dp)
                    .clickable { onPlaySong(song) }
            ) {
                JeneArtwork(
                    model = song.artworkUri ?: song.data,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    cornerRadius = 16.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentlyAddedSection(
    songs: List<Song>,
    favoriteSongs: List<Song>,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (Song, Boolean) -> Unit
) {
    Text(
        text = "Recently Added",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        songs.forEach { song ->
            val isFav = favoriteSongs.any { it.id == song.id }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlaySong(song) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    JeneArtwork(
                        model = song.artworkUri ?: song.data,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        cornerRadius = 8.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = { onToggleFavorite(song, isFav) }) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumsSection(albums: List<Album>, onNavigateToAlbum: (String, String) -> Unit) {
    Text(
        text = "Albums",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        albums.chunked(2).forEach { rowAlbums ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowAlbums.forEach { album ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToAlbum(album.name, album.artist) }
                    ) {
                        JeneArtwork(
                            model = album.artworkUri ?: "",
                            modifier = Modifier.fillMaxSize(),
                            cornerRadius = 16.dp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY
                                    )
                                )
                        )
                        Text(
                            text = album.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (rowAlbums.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PlaylistsSection(playlists: List<PlaylistWithSongs>) {
    Text(
        text = "Playlists",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(playlists, key = { it.playlist.id }) { playlistWithSongs ->
            val playlist = playlistWithSongs.playlist
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF333333))
            ) {
                if (playlist.artworkUri != null) {
                    JeneArtwork(
                        model = playlist.artworkUri,
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 12.dp
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyLibraryPrompt(onScanLibrary: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your music library is empty",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add music to your device and JENE will find it automatically.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        JeneGlassButton(
            text = "Scan Library",
            onClick = onScanLibrary
        )
    }
}
"""

with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
