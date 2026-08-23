package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel
import com.jene.music.data.model.Song
import com.jene.music.ui.components.JeneArtwork

@Composable
fun LibraryScreen(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit, onNavigateToPlaylist: (Long) -> Unit) {
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("Playlists") }
    val filters = listOf("Playlists", "Songs", "Albums", "Artists", "Downloaded")

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )
        }

        // Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // List Content based on filter
        if (selectedFilter == "Playlists" || selectedFilter == "Songs") {
            item {
                LibraryListItem(
                    title = "Liked Songs",
                    subtitle = "Playlist • ${allSongs.filter { it.isFavorite }.size} songs",
                    icon = Icons.Filled.Favorite,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = { viewModel.shuffleAndPlay(allSongs.filter { it.isFavorite }) }
                )
            }
            
            items(allPlaylists) { playlistWithSongs ->
                LibraryListItem(
                    title = playlistWithSongs.playlist.name,
                    subtitle = "Playlist • ${playlistWithSongs.songs.size} songs",
                    artworkUri = playlistWithSongs.playlist.artworkUri ?: playlistWithSongs.songs.firstOrNull()?.artworkUri ?: playlistWithSongs.songs.firstOrNull()?.data,
                    onClick = { onNavigateToPlaylist(playlistWithSongs.playlist.id) }
                )
            }
        }
        
        if (selectedFilter == "Albums" || selectedFilter == "Songs") {
            items(albums) { album ->
                LibraryListItem(
                    title = album.name,
                    subtitle = "Album • ${album.artist}",
                    artworkUri = album.artworkUri ?: album.songs.firstOrNull()?.data,
                    onClick = { onNavigateToAlbum(album.name, album.artist) }
                )
            }
        }
    }
}

@Composable
fun LibraryListItem(
    title: String,
    subtitle: String,
    artworkUri: String? = null,
    icon: ImageVector? = null,
    iconBgColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    iconTintColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(28.dp))
            }
        } else {
            JeneArtwork(
                model = artworkUri,
                modifier = Modifier.size(64.dp),
                cornerRadius = 8.dp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
