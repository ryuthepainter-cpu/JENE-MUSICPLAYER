sed -i '/val scrollState = rememberScrollState()/a \
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }\n    if (songToAddToPlaylist != null) {\n        com.jene.music.ui.components.AddToPlaylistDialog(\n            song = songToAddToPlaylist!!,\n            viewModel = viewModel,\n            onDismiss = { songToAddToPlaylist = null }\n        )\n    }' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt

sed -i 's/IconButton(onClick = { \/\* TODO: Queue \*\/ }) {/IconButton(onClick = { songToAddToPlaylist = song }) {/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
