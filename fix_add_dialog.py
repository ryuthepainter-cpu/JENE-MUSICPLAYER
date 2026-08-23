import re

with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "r") as f:
    content = f.read()

content = content.replace("items(playlists, key = { it.id }) { playlist ->",
                          "items(playlists, key = { it.playlist.id }) { playlistWithSongs ->")
content = content.replace("headlineContent = { Text(playlist.name) },",
                          "headlineContent = { Text(playlistWithSongs.playlist.name) },")
content = content.replace("if (playlist.description != null) {",
                          "if (playlistWithSongs.playlist.description != null) {")
content = content.replace("{ Text(playlist.description) }",
                          "{ Text(playlistWithSongs.playlist.description!!) }")
content = content.replace("viewModel.addSongToPlaylist(playlist.id, song.id)",
                          "viewModel.addSongToPlaylist(playlistWithSongs.playlist.id, song.id)")

with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "w") as f:
    f.write(content)
