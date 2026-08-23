package com.jene.music.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.data.model.Song
import com.jene.music.ui.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.jene.music.ui.screens.CreatePlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistDialog(
    song: Song,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val playlists by viewModel.allPlaylists.collectAsStateWithLifecycle(emptyList())

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, desc ->
                viewModel.createPlaylist(name, desc.takeIf { it.isNotEmpty() })
                showCreateDialog = false
            }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add to Playlist",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    ListItem(
                        headlineContent = { Text("New Playlist") },
                        leadingContent = {
                            Icon(Icons.Filled.Add, contentDescription = "Create new playlist")
                        },
                        modifier = Modifier.clickable { showCreateDialog = true }
                    )
                }
                if (playlists.isEmpty()) {
                    item {
                        Text(
                            text = "No other playlists found.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(playlists, key = { it.playlist.id }) { playlistWithSongs ->
                        ListItem(
                            headlineContent = { Text(playlistWithSongs.playlist.name) },
                            supportingContent = if (playlistWithSongs.playlist.description != null) {
                                { Text(playlistWithSongs.playlist.description!!) }
                            } else null,
                            modifier = Modifier.clickable {
                                viewModel.addSongToPlaylist(playlistWithSongs.playlist.id, song.id)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
