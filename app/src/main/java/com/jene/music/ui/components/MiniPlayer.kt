package com.jene.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel

@Composable
fun MiniPlayer(viewModel: MainViewModel, modifier: Modifier = Modifier, onNavigateToNowPlaying: () -> Unit = {}) {
    val playerState by viewModel.playerController.playerState.collectAsStateWithLifecycle()
    val currentSong = playerState.currentSong
    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()
    val playbackState = playerState
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    
    if (currentSong != null) {
        val song = currentSong!!
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp) // 8px above bottom navigation
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .clickable { onNavigateToNowPlaying() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JeneArtwork(
                    model = song.artworkUri ?: song.data,
                    modifier = Modifier.size(40.dp),
                    cornerRadius = 8.dp
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
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
                
                val isFavorite = favoriteSongs.any { it.id == song.id }
                IconButton(onClick = { viewModel.toggleFavorite(song.copy(isFavorite = isFavorite)) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { viewModel.playerController.playPause() }) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Progress Bar at the very bottom of the mini player
            val progress = if (playbackState.duration > 0) {
                playbackPosition.toFloat() / playbackState.duration.toFloat()
            } else 0f
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 2.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )
        }
    }
}
