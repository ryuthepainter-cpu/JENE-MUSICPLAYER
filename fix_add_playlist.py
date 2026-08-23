import re

with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "r") as f:
    content = f.read()

content = content.replace("import com.jene.music.ui.MainViewModel",
                          "import com.jene.music.ui.MainViewModel\nimport androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.Add\nimport com.jene.music.ui.screens.CreatePlaylistDialog")

content = content.replace("val coroutineScope = rememberCoroutineScope()",
                          "val coroutineScope = rememberCoroutineScope()\n    var showCreateDialog by remember { mutableStateOf(false) }\n\n    if (showCreateDialog) {\n        CreatePlaylistDialog(\n            onDismiss = { showCreateDialog = false },\n            onCreate = { name ->\n                viewModel.createPlaylist(name)\n                showCreateDialog = false\n            }\n        )\n    }")

content = content.replace("""            if (playlists.isEmpty()) {
                Text(
                    text = "No playlists found. Go to Playlists to create one.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(playlists, key = { it.playlist.id }) { playlistWithSongs ->""",
"""            LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
                    items(playlists, key = { it.playlist.id }) { playlistWithSongs ->""")

content = content.replace("""                    }
                }
            }""",
"""                    }
                }
            }""")

with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "w") as f:
    f.write(content)
