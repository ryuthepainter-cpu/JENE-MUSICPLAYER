import re

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("items(playlists, key = { it.id }) { playlist ->",
                          "items(playlists, key = { it.playlist.id }) { playlistWithSongs ->")
content = content.replace("onNavigateToPlaylist(playlist.id)", "onNavigateToPlaylist(playlistWithSongs.playlist.id)")
content = content.replace("text = playlist.name,", "text = playlistWithSongs.playlist.name,")

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt", "w") as f:
    f.write(content)
