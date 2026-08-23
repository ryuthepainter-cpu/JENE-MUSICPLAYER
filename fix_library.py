import re

with open("app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt", "r") as f:
    content = f.read()

# Remove 'Add' chip
content = re.sub(r'item \{\s+Box\([\s\S]*?Text\("Add"[\s\S]*?\}\s+\}', '', content)

# Remove 'Sort Bar'
content = re.sub(r'// Sort Bar[\s\S]*?Spacer\(modifier = Modifier\.height\(16\.dp\)\)\s+\}', '', content)

# Pass onNavigateToPlaylist to LibraryScreen
content = content.replace("fun LibraryScreen(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit)",
                          "fun LibraryScreen(viewModel: MainViewModel, onNavigateToAlbum: (String, String) -> Unit, onNavigateToPlaylist: (Long) -> Unit)")

# Fix Liked Songs click
content = content.replace("onClick = { /* Navigate to Liked Songs */ }", 
                          "onClick = { viewModel.shuffleAndPlay(allSongs.filter { it.isFavorite }) }")

# Fix Playlist click
content = content.replace("onClick = { /* Navigate to Playlist */ }", 
                          "onClick = { onNavigateToPlaylist(playlistWithSongs.playlist.id) }")

with open("app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt", "w") as f:
    f.write(content)
