import re

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# Remove Queue and More buttons
content = content.replace("""                Row {
                    IconButton(onClick = { /* Queue */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Queue",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* More */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }""", """                // Removed unused Queue and More buttons""")

# Favorite state
content = content.replace("val currentSong = playerState.currentSong ?: return",
                          "val currentSong = playerState.currentSong ?: return\n    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()\n    val isFavorite = favoriteSongs.any { it.id == currentSong.id }")

# Favorite button
content = content.replace("""                IconButton(onClick = { /* Toggle Favorite */ }) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder, // Replace with Filled if favored
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }""",
"""                IconButton(onClick = { viewModel.toggleFavorite(currentSong.copy(isFavorite = isFavorite)) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }""")

# Repeat button
content = content.replace("""                IconButton(onClick = { /* Repeat toggle */ }) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }""",
"""                IconButton(onClick = { 
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
                }""")

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(content)
