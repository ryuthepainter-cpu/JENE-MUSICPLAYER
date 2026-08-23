import re

with open("app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.filled.FavoriteBorder",
                          "import androidx.compose.material.icons.filled.FavoriteBorder\nimport androidx.compose.material.icons.filled.Favorite")

content = content.replace("val playbackState = playerState",
                          "val playbackState = playerState\n    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()")

content = content.replace("""                IconButton(onClick = { /* Toggle favorite - implement later if needed */ }) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }""",
"""                val isFavorite = favoriteSongs.any { it.id == song.id }
                IconButton(onClick = { viewModel.toggleFavorite(song.copy(isFavorite = isFavorite)) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }""")

with open("app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt", "w") as f:
    f.write(content)
