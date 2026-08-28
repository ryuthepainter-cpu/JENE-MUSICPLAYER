import os

new_code = """package com.jene.music.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jene.music.core.player.PlayerState
import com.jene.music.data.model.Song
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val playerState by viewModel.playerController.playerState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()
    val currentSong = playerState.currentSong ?: return
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val isFavorite = favoriteSongs.any { it.id == currentSong.id }
    val lyrics by viewModel.lyricsState.collectAsStateWithLifecycle()
    
    var showQueue by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050505))) {
        NowPlayingBackground(currentSong)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            NowPlayingTopBar(onBack = onBack, onOpenQueue = { showQueue = true })
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val artworkSize = maxWidth * 0.85f
                    NowPlayingArtwork(
                        currentSong = currentSong,
                        size = artworkSize,
                        onSwipeLeft = { viewModel.playerController.skipToNext() },
                        onSwipeRight = { viewModel.playerController.skipToPrevious() }
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    NowPlayingInfo(
                        currentSong = currentSong,
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite(currentSong.copy(isFavorite = isFavorite)) },
                        onMore = { /* Open more dialog */ }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    NowPlayingProgress(
                        playerState = playerState,
                        playbackPosition = playbackPosition,
                        onSeek = { newPosition -> viewModel.playerController.seekTo(newPosition) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    NowPlayingControls(viewModel, playerState)
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    NowPlayingLyricsSection(lyricsState = lyrics, currentPosition = playbackPosition)
                    
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
        
        if (showQueue) {
            ModalBottomSheet(
                onDismissRequest = { showQueue = false },
                containerColor = Color(0xFF141414).copy(alpha = 0.95f),
                scrimColor = Color.Black.copy(alpha = 0.6f),
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF555555)) }
            ) {
                QueueSheetContent(
                    playerState = playerState,
                    onSongClick = { index ->
                        viewModel.playerController.skipToQueueItem(index)
                        showQueue = false
                    }
                )
            }
        }
    }
}

@Composable
private fun NowPlayingBackground(currentSong: Song) {
    AnimatedContent(
        targetState = currentSong.id,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "BackgroundTransition"
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentSong.artworkUri ?: currentSong.data)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp)
            )
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212).copy(alpha = 0.85f)))
        }
    }
}

@Composable
private fun NowPlayingTopBar(onBack: () -> Unit, onOpenQueue: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Collapse",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NOW PLAYING",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                color = Color(0xFFDDDDDD)
            )
        }
        
        IconButton(
            onClick = onOpenQueue,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = "Queue",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun NowPlayingArtwork(currentSong: Song, size: androidx.compose.ui.unit.Dp, onSwipeLeft: () -> Unit, onSwipeRight: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember(currentSong.id) { Animatable(0f) }
    var hasCommitted by remember(currentSong.id) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = offsetX.value / 40f
            }
            .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(32.dp))
            .pointerInput(currentSong.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (!hasCommitted) {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                            }
                        }
                    },
                    onDragCancel = {
                        if (!hasCommitted) {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                            }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        if (hasCommitted) return@detectHorizontalDragGestures
                        
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                        }
                        if (offsetX.value > 250f) {
                            hasCommitted = true
                            onSwipeRight()
                        } else if (offsetX.value < -250f) {
                            hasCommitted = true
                            onSwipeLeft()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        JeneArtwork(
            model = currentSong.artworkUri ?: currentSong.data,
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 32.dp
        )
    }
}

@Composable
private fun NowPlayingInfo(currentSong: Song, isFavorite: Boolean, onToggleFavorite: () -> Unit, onMore: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentSong.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSong.artist,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFFAAAAAA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.White else Color(0xFF888888),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun NowPlayingProgress(playerState: PlayerState, playbackPosition: Long, onSeek: (Long) -> Unit) {
    var sliderPosition by remember { mutableStateOf<Float?>(null) }
    val progress = sliderPosition ?: if (playerState.duration > 0) {
        playbackPosition.toFloat() / playerState.duration.toFloat()
    } else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(if (sliderPosition != null) (progress * playerState.duration).toLong() else playbackPosition),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFFAAAAAA)
            )
            Text(
                text = formatTime(playerState.duration),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFFAAAAAA)
            )
        }
        
        ThinProgressBar(
            progress = progress,
            onProgressChange = { sliderPosition = it },
            onProgressChangeFinished = {
                sliderPosition?.let { finalProgress ->
                    val newPosition = (finalProgress * playerState.duration).roundToLong()
                    onSeek(newPosition)
                    sliderPosition = null
                }
            }
        )
    }
}

@Composable
private fun ThinProgressBar(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { onProgressChangeFinished() },
                    onDragCancel = { onProgressChangeFinished() },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        onProgressChange(newProgress)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        onProgressChange(newProgress)
                        onProgressChangeFinished()
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.15f))
        )
        // Progress
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
        // Thumb
        Box(
            modifier = Modifier
                .padding(start = (maxWidth * progress) - 6.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(Color.White)
                .shadow(4.dp, CircleShape)
        )
    }
}

@Composable
private fun NowPlayingControls(viewModel: MainViewModel, playerState: PlayerState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.playerController.setShuffleModeEnabled(!playerState.shuffleModeEnabled) }) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = "Shuffle",
                tint = if (playerState.shuffleModeEnabled) Color.White else Color(0xFF666666),
                modifier = Modifier.size(24.dp)
            )
        }
        
        IconButton(
            onClick = { viewModel.playerController.skipToPrevious() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { viewModel.playerController.playPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
        
        IconButton(
            onClick = { viewModel.playerController.skipToNext() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
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
                tint = if (playerState.repeatMode == androidx.media3.common.Player.REPEAT_MODE_OFF) Color(0xFF666666) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun NowPlayingLyricsSection(lyricsState: com.jene.music.data.model.LyricsState, currentPosition: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF222222).copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LYRICS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                    color = Color(0xFFAAAAAA)
                )
                Icon(
                    imageVector = Icons.Filled.OpenInFull,
                    contentDescription = "Expand Lyrics",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            when (lyricsState) {
                is com.jene.music.data.model.LyricsState.Loading -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
                is com.jene.music.data.model.LyricsState.Error -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "Failed to load lyrics.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF666666)
                        )
                    }
                }
                is com.jene.music.data.model.LyricsState.NoLyrics -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "No lyrics available for this song.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF666666)
                        )
                    }
                }
                is com.jene.music.data.model.LyricsState.Loaded -> {
                    val lyrics = lyricsState.lyrics
                    val activeIndex = lyrics.indexOfLast { currentPosition >= it.startTimeMs }.coerceAtLeast(0)
                    
                    Column(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                        val start = maxOf(0, activeIndex - 1)
                        val end = minOf(lyrics.size - 1, activeIndex + 2)
                        
                        for (i in start..end) {
                            val isActive = i == activeIndex
                            Text(
                                text = lyrics[i].text,
                                style = if (isActive) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp) 
                                        else MaterialTheme.typography.titleMedium,
                                color = if (isActive) Color.White else Color(0xFF777777),
                                modifier = Modifier.padding(vertical = 6.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QueueSheetContent(playerState: PlayerState, onSongClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Up Next",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
        ) {
            itemsIndexed(playerState.currentPlaylist) { index, song ->
                val isPlaying = song.id == playerState.currentSong?.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSongClick(index) }
                        .background(if (isPlaying) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                        .padding(8.dp),
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
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isPlaying) Color.White else Color(0xFFDDDDDD),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFAAAAAA),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Playing",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
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
    f.write(new_code)
