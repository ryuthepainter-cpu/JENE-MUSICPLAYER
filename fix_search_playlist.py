import re

with open("app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import com.jene.music.ui.components.SongListItem",
                          "import com.jene.music.ui.components.SongListItem\nimport com.jene.music.ui.components.AddToPlaylistDialog\nimport com.jene.music.data.model.Song")

content = content.replace("var searchQuery by remember { mutableStateOf(\"\") }",
                          "var searchQuery by remember { mutableStateOf(\"\") }\n    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }\n\n    if (songToAddToPlaylist != null) {\n        AddToPlaylistDialog(\n            song = songToAddToPlaylist!!,\n            viewModel = viewModel,\n            onDismiss = { songToAddToPlaylist = null }\n        )\n    }")

content = content.replace("onAddToPlaylist = { /* Handle add to playlist */ }",
                          "onAddToPlaylist = { songToAddToPlaylist = song }")

with open("app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt", "w") as f:
    f.write(content)
