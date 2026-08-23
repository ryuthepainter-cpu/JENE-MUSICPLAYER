package com.jene.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel

@Composable
fun MiniPlayer(viewModel: MainViewModel, modifier: Modifier = Modifier, onNavigateToNowPlaying: () -> Unit = {}) {
    val currentSong by viewModel.musicServiceConnection.currentSong.collectAsStateWithLifecycle()
    val playbackState by viewModel.musicServiceConnection.playbackState.collectAsStateWithLifecycle()
    
    if (currentSong != null) {
        val song = currentSong!!
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onNavigateToNowPlaying() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JeneArtwork(
                model = song.artworkUri ?: song.data,
                modifier = Modifier.size(48.dp),
                cornerRadius = 16.dp
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
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
            
            IconButton(onClick = { viewModel.musicServiceConnection.playPause() }) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(onClick = { viewModel.musicServiceConnection.skipToNext() }) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
