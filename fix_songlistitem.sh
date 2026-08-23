sed -i 's/onFavoriteClick: () -> Unit,/onFavoriteClick: () -> Unit,\n    onAddToPlaylist: (() -> Unit)? = null,/' app/src/main/java/com/jene/music/ui/components/SongListItem.kt

sed -i '/import androidx.compose.runtime.Composable/a \
import androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember' app/src/main/java/com/jene/music/ui/components/SongListItem.kt

sed -i 's/IconButton(onClick = { \/\* TODO: Show options menu \*\/ }) {/var showMenu by remember { mutableStateOf(false) }\n        Box {\n        IconButton(onClick = { showMenu = true }) {/' app/src/main/java/com/jene/music/ui/components/SongListItem.kt

sed -i 's/contentDescription = "Options",/contentDescription = "Options",/' app/src/main/java/com/jene/music/ui/components/SongListItem.kt

sed -i '/tint = MaterialTheme.colorScheme.onSurfaceVariant/a \
            }\n            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {\n                if (onAddToPlaylist != null) {\n                    DropdownMenuItem(\n                        text = { Text("Add to playlist") },\n                        onClick = { showMenu = false; onAddToPlaylist() }\n                    )\n                }\n            }\n        }' app/src/main/java/com/jene/music/ui/components/SongListItem.kt
