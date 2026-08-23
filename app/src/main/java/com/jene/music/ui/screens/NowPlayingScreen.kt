package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jene.music.data.LyricLine
import com.jene.music.data.LyricsParser
import com.jene.music.data.Song
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import kotlinx.coroutines.launch

@Composable
fun NowPlayingScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val currentSong by viewModel.musicServiceConnection.currentSong.collectAsStateWithLifecycle()
    val playbackState by viewModel.musicServiceConnection.playbackState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    if (songToAddToPlaylist != null) {
        com.jene.music.ui.components.AddToPlaylistDialog(
            song = songToAddToPlaylist!!,
            viewModel = viewModel,
            onDismiss = { songToAddToPlaylist = null }
        )
    }

    if (currentSong == null) return
    val song = currentSong!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient background
        AsyncImage(
            model = song.artworkUri ?: song.data,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f,
        )

        // Glass overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { songToAddToPlaylist = song }) {
                    Icon(
                        imageVector = Icons.Filled.QueueMusic,
                        contentDescription = "Queue",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Artwork
            JeneArtwork(
                model = song.artworkUri ?: song.data,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Title & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { viewModel.toggleFavorite(song) }) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress
            Slider(
                value = playbackState.playbackPosition.toFloat(),
                onValueChange = { viewModel.musicServiceConnection.seekTo(it.toLong()) },
                valueRange = 0f..(playbackState.duration.toFloat().takeIf { it > 0 } ?: 100f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.musicServiceConnection.setShuffleModeEnabled(!playbackState.shuffleModeEnabled) }) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.shuffleModeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { viewModel.musicServiceConnection.skipToPrevious() }) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { viewModel.musicServiceConnection.playPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                IconButton(onClick = { viewModel.musicServiceConnection.skipToNext() }) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                IconButton(onClick = { /* TODO: Repeat mode */ }) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Lyrics Section
            LyricsSection(song = song, currentPosition = playbackState.playbackPosition, viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun LyricsSection(song: Song, currentPosition: Long, viewModel: MainViewModel) {
    var lyrics by remember(song) { mutableStateOf<List<LyricLine>?>(null) }
    var hasAttemptedLoad by remember(song) { mutableStateOf(false) }
    var reloadTrigger by remember(song) { mutableStateOf(0) }
    
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.saveLyricUri(song.id, uri.toString())
            reloadTrigger++
        }
    }
    
    LaunchedEffect(song, reloadTrigger) {
        lyrics = viewModel.getLyricsForSong(song)
        hasAttemptedLoad = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp, max = 500.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Text(
            text = "Lyrics",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (!hasAttemptedLoad) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else if (lyrics.isNullOrEmpty()) {
            Text(
                text = "Lyrics unavailable",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "JENE can display lyrics embedded in your music files, local .LRC files, or a SAF lyrics directory.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { launcher.launch(arrayOf("*/*")) }) {
                Text("Select .LRC File for this Song")
            }
        } else {
            val listState = androidx.compose.foundation.lazy.rememberLazyListState()
            val activeIndex = remember(currentPosition, lyrics) {
                val lines = lyrics!!
                var bestIndex = -1
                for (i in lines.indices) {
                    if (currentPosition >= lines[i].startTimeMs) bestIndex = i
                    else break
                }
                bestIndex
            }
            
            LaunchedEffect(activeIndex) {
                if (activeIndex >= 0) {
                    listState.animateScrollToItem(kotlin.math.max(0, activeIndex - 2))
                }
            }
            
            androidx.compose.foundation.lazy.LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(lyrics!!.size) { index ->
                    val line = lyrics!![index]
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex
                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isActive) MaterialTheme.colorScheme.primary
                                 else if (isPast) MaterialTheme.colorScheme.onSurfaceVariant
                                 else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
