import os

with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# I will rewrite HomeScreen to use smaller components for the main sections.

new_content = """package com.jene.music.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.data.model.Song
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = 24.dp)
    ) {
        item {
            HomeGreetingSection(onNavigateToSettings = onNavigateToSettings)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (allSongs.isEmpty()) {
            item {
                EmptyLibraryPrompt(onScanLibrary = { viewModel.scanLibrary() })
            }
            return@LazyColumn
        }

        item {
            QuickAccessGrid(
                onPlayFavorites = { viewModel.shuffleAndPlay(favoriteSongs) },
                onPlayRecent = { viewModel.shuffleAndPlay(recentlyPlayed) },
                onPlayAll = { viewModel.shuffleAndPlay(allSongs) }
            )
        }

        if (recentlyPlayed.isNotEmpty()) {
            item {
                JumpBackInSection(
                    recentlyPlayed = recentlyPlayed,
                    onPlaySong = { song -> viewModel.playSong(song, recentlyPlayed) }
                )
            }
        }

        if (recentlyAdded.isNotEmpty()) {
            item {
                Text(
                    text = "Recently Added",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }
            items(recentlyAdded.take(10), key = { "recent_${it.id}" }) { song ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    RecentlyAddedRow(
                        song = song,
                        onClick = { viewModel.playSong(song, recentlyAdded) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HomeGreetingSection(onNavigateToSettings: () -> Unit) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Row {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            .padding(horizontal = 24.dp, vertical = 32.dp),
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

@Composable
private fun QuickAccessGrid(
    onPlayFavorites: () -> Unit,
    onPlayRecent: () -> Unit,
    onPlayAll: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAccessCard(
                title = "Liked Songs",
                icon = Icons.Filled.Favorite,
                iconTint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f),
                onClick = onPlayFavorites
            )
            QuickAccessCard(
                title = "Recently Played",
                icon = Icons.Filled.History,
                iconTint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f),
                onClick = onPlayRecent
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAccessCard(
                title = "Downloads",
                icon = Icons.Filled.Download,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = onPlayAll
            )
            QuickAccessCard(
                title = "Top Mixes",
                icon = Icons.Filled.MusicNote,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = onPlayAll
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun JumpBackInSection(recentlyPlayed: List<Song>, onPlaySong: (Song) -> Unit) {
    Text(
        text = "Jump Back In",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, bottom = 16.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(recentlyPlayed, key = { it.id }) { song ->
            JumpBackInCard(
                song = song,
                onClick = { onPlaySong(song) }
            )
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun QuickAccessCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(56.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun JumpBackInCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            JeneArtwork(
                model = song.artworkUri ?: song.data,
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 8.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecentlyAddedRow(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JeneArtwork(
            model = song.artworkUri ?: song.data,
            modifier = Modifier.size(48.dp),
            cornerRadius = 4.dp
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
"""

with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "w") as f:
    f.write(new_content)

