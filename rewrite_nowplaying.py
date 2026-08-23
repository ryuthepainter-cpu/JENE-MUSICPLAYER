import os

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# I will write a comprehensive, decomposed NowPlayingScreen preserving all logic

new_content = """package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jene.music.data.model.Song
import com.jene.music.core.player.PlayerState
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val playerState by viewModel.playerController.playerState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()
    val currentSong = playerState.currentSong ?: return
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val isFavorite = favoriteSongs.any { it.id == currentSong.id }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050505))) {
        NowPlayingBackground(currentSong)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp)
        ) {
            NowPlayingTopBar(onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))
            NowPlayingArtwork(currentSong)
            Spacer(modifier = Modifier.height(32.dp))
            NowPlayingInfo(
                currentSong = currentSong,
                isFavorite = isFavorite,
                onToggleFavorite = { viewModel.toggleFavorite(currentSong.copy(isFavorite = isFavorite)) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            NowPlayingProgress(
                playerState = playerState,
                playbackPosition = playbackPosition,
                onSeek = { newPosition -> viewModel.playerController.seekTo(newPosition) }
            )
            Spacer(modifier = Modifier.weight(1f))
            NowPlayingControls(viewModel, playerState)
            Spacer(modifier = Modifier.height(24.dp))
            NowPlayingLyricsPreview()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NowPlayingBackground(currentSong: Song) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(currentSong.artworkUri ?: currentSong.data)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .blur(80.dp)
    )
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)))
}

@Composable
private fun NowPlayingTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "NOW PLAYING",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NowPlayingArtwork(currentSong: Song) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
    ) {
        JeneArtwork(
            model = currentSong.artworkUri ?: currentSong.data,
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 24.dp
        )
    }
}

@Composable
private fun NowPlayingInfo(currentSong: Song, isFavorite: Boolean, onToggleFavorite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentSong.title,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSong.artist,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NowPlayingProgress(playerState: PlayerState, playbackPosition: Long, onSeek: (Long) -> Unit) {
    var sliderPosition by remember { mutableStateOf<Float?>(null) }
    val progress = sliderPosition ?: if (playerState.duration > 0) {
        playbackPosition.toFloat() / playerState.duration.toFloat()
    } else 0f

    Slider(
        value = progress,
        onValueChange = { newProgress -> sliderPosition = newProgress },
        onValueChangeFinished = {
            sliderPosition?.let { finalProgress ->
                val newPosition = (finalProgress * playerState.duration).roundToLong()
                onSeek(newPosition)
                sliderPosition = null
            }
        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primaryContainer,
            activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatTime(playbackPosition),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "-" + formatTime(playerState.duration - playbackPosition),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NowPlayingControls(viewModel: MainViewModel, playerState: PlayerState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.playerController.setShuffleModeEnabled(!playerState.shuffleModeEnabled) }) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = "Shuffle",
                tint = if (playerState.shuffleModeEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { viewModel.playerController.skipToPrevious() }, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { viewModel.playerController.playPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        IconButton(onClick = { viewModel.playerController.skipToNext() }, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp)
            )
        }

        IconButton(onClick = {
            val nextMode = when (playerState.repeatMode) {
                androidx.media3.common.Player.REPEAT_MODE_OFF -> androidx.media3.common.Player.REPEAT_MODE_ALL
                androidx.media3.common.Player.REPEAT_MODE_ALL -> androidx.media3.common.Player.REPEAT_MODE_ONE
                else -> androidx.media3.common.Player.REPEAT_MODE_OFF
            }
            viewModel.playerController.setRepeatMode(nextMode)
        }) {
            Icon(
                imageVector = if (playerState.repeatMode == androidx.media3.common.Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                contentDescription = "Repeat",
                tint = if (playerState.repeatMode == androidx.media3.common.Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
private fun NowPlayingLyricsPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121414).copy(alpha = 0.4f))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LYRICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Filled.OpenInFull, contentDescription = "Expand", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "I've been on my own for long enough...",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Maybe you can show me how to love",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatTime(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
"""

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(new_content)
