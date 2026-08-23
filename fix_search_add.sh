sed -i '/import com.jene.music.ui.components.SongListItem/a \
import com.jene.music.ui.components.AddToPlaylistDialog\nimport com.jene.music.data.Song' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt

sed -i '/val searchResults by/a \
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }\n\n    if (songToAddToPlaylist != null) {\n        AddToPlaylistDialog(\n            song = songToAddToPlaylist!!,\n            viewModel = viewModel,\n            onDismiss = { songToAddToPlaylist = null }\n        )\n    }' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt

sed -i 's/onFavoriteClick = { viewModel.toggleFavorite(song) }/onFavoriteClick = { viewModel.toggleFavorite(song) },\n                    onAddToPlaylist = { songToAddToPlaylist = song }/' app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt
