import re

with open("app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
"""            items(allPlaylists) { playlist ->
                LibraryListItem(
                    title = playlist.name,
                    subtitle = "Playlist",
                    artworkUri = playlist.artworkUri,
                    onClick = { /* Navigate to Playlist */ }
                )
            }""",
"""            items(allPlaylists) { playlistWithSongs ->
                LibraryListItem(
                    title = playlistWithSongs.playlist.name,
                    subtitle = "Playlist • ${playlistWithSongs.songs.size} songs",
                    artworkUri = playlistWithSongs.playlist.artworkUri ?: playlistWithSongs.songs.firstOrNull()?.artworkUri ?: playlistWithSongs.songs.firstOrNull()?.data,
                    onClick = { /* Navigate to Playlist */ }
                )
            }"""
)

with open("app/src/main/java/com/jene/music/ui/screens/LibraryScreen.kt", "w") as f:
    f.write(content)
