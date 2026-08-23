package com.jene.music.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.data.model.Playlist
import com.jene.music.data.model.Song
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.JeneArtwork
import com.jene.music.ui.components.SongListItem
import kotlinx.coroutines.launch
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    viewModel: MainViewModel,
    playlistId: Long,
    onBack: () -> Unit
) {
    val playlist by viewModel.repository.getPlaylistById(playlistId).collectAsStateWithLifecycle(null)
    val songs by viewModel.repository.getSongsInPlaylist(playlistId).collectAsStateWithLifecycle(emptyList())
    val coroutineScope = rememberCoroutineScope()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (playlist == null) return

    if (showEditDialog) {
        EditPlaylistDialog(
            playlist = playlist!!,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                coroutineScope.launch {
                    viewModel.repository.updatePlaylist(updated)
                    showEditDialog = false
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Playlist") },
            text = { Text("Are you sure you want to delete '${playlist?.name}'? This will not delete your music files.") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        viewModel.repository.deletePlaylist(playlistId)
                        onBack()
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit Playlist") },
                                onClick = { showMenu = false; showEditDialog = true },
                                leadingIcon = { Icon(Icons.Filled.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Playlist") },
                                onClick = { showMenu = false; showDeleteConfirm = true },
                                leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                JeneArtwork(
                    model = playlist?.artworkUri ?: songs.firstOrNull()?.artworkUri ?: songs.firstOrNull()?.data,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1f)
                        .align(Alignment.CenterHorizontally),
                    cornerRadius = 16.dp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = playlist?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                if (!playlist?.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = playlist?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${songs.size} tracks",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (songs.isNotEmpty()) {
                                viewModel.playerController.setShuffleModeEnabled(false)
                                viewModel.playSong(songs.first(), songs)
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
                            if (songs.isNotEmpty()) {
                                viewModel.shuffleAndPlay(songs)
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
        
        if (songs.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "This playlist is empty",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add songs from your library to start listening.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
                SongListItemWithPlaylistMenu(
                    song = song,
                    onClick = { viewModel.playSong(song, songs) },
                    onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onRemove = { 
                        coroutineScope.launch {
                            viewModel.repository.removeSongFromPlaylist(playlistId, song.id)
                        }
                    },
                    onMoveUp = if (index > 0) {
                        {
                            coroutineScope.launch {
                                val prev = songs[index - 1]
                                viewModel.repository.updateSongPosition(playlistId, song.id, index - 1)
                                viewModel.repository.updateSongPosition(playlistId, prev.id, index)
                            }
                        }
                    } else null,
                    onMoveDown = if (index < songs.size - 1) {
                        {
                            coroutineScope.launch {
                                val next = songs[index + 1]
                                viewModel.repository.updateSongPosition(playlistId, song.id, index + 1)
                                viewModel.repository.updateSongPosition(playlistId, next.id, index)
                            }
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
fun SongListItemWithPlaylistMenu(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JeneArtwork(
            model = song.artworkUri ?: song.data,
            modifier = Modifier.size(48.dp),
            cornerRadius = 8.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
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
        
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                if (onMoveUp != null) {
                    DropdownMenuItem(
                        text = { Text("Move Up") },
                        onClick = { showMenu = false; onMoveUp() },
                        leadingIcon = { Icon(Icons.Filled.ArrowUpward, null) }
                    )
                }
                if (onMoveDown != null) {
                    DropdownMenuItem(
                        text = { Text("Move Down") },
                        onClick = { showMenu = false; onMoveDown() },
                        leadingIcon = { Icon(Icons.Filled.ArrowDownward, null) }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Remove from playlist") },
                    onClick = { showMenu = false; onRemove() },
                    leadingIcon = { Icon(Icons.Filled.RemoveCircleOutline, null, tint = MaterialTheme.colorScheme.error) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistDialog(playlist: Playlist, onDismiss: () -> Unit, onSave: (Playlist) -> Unit) {
    var name by remember { mutableStateOf(playlist.name) }
    var description by remember { mutableStateOf(playlist.description ?: "") }
    var artworkUri by remember { mutableStateOf(playlist.artworkUri) }
    var isError by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            artworkUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Playlist") },
        text = {
            Column {
                JeneArtwork(
                    model = artworkUri,
                    modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally).clickable { launcher.launch(arrayOf("image/*")) },
                    cornerRadius = 8.dp
                )
                Text("Tap to change image", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; isError = false },
                    label = { Text("Name") },
                    isError = isError,
                    singleLine = true
                )
                if (isError) {
                    Text("Name cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isEmpty()) {
                        isError = true
                    } else {
                        onSave(playlist.copy(name = trimmed, description = description.ifEmpty { null }, artworkUri = artworkUri))
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
